(ns hypernova.activities.api
  (:import [hypernova Universe Ship Mass Hull]
	   [java.io PushbackReader InputStreamReader]))

(defonce *universe* (Universe/get))

;;; these variables are bound dynamically when a given scenario is
;;; realized. They are bound to values supplied by java at realization
;;; time.
(def *x* 0)
(def *y* 0)

(defn parts [kind]
  #(Ship/get kind))

(defn ship [kind]
  #(Ship. kind))

(defn mass [kind]
  #(Mass. kind))

(defn message [string]
  (.queueMessage *universe* (str string)))

(defprotocol Positional
  "a thing with position"
  (position [this]))

(defprotocol Velocital
  "a thing with velocity"
  (velocity [this]))

(defrecord Vector2
  [x y]

  Positional
  (position [this] this)

  Velocital
  (velocity [this] this))

(defn position-absolute [x y]
  "create a position in absolute universe coords"
  (Vector2. x y))

(defn position-relative [x y]
  "create a position that's relative to the center we were given"
  (position-absolute (+ *x* x)
		     (+ *y* y)))

(defn velocity [x y]
  "creates a velocity"
  (Vector2. x y))

(defn position-x [p]
  (:x p))

(defn position-y [p]
  (:y p))

(defn velocity-x [v]
  (:x v))

(defn velocity-y [v]
  (:y v))

(defn add [p1 p2]
  "add two positions"
  (position-absolute (+ (position-x p1) (position-x p2))
		     (+ (position-y p1) (position-y p2))))

(defn sub [p1 p2]
  "subtract two positions"
  (position-absolute (- (position-x p1) (position-x p2))
		     (- (position-y p1) (position-y p2))))

(defn scale [p s]
  "scale the position by s"
  (position-absolute (* (position-x p) s)
		     (* (position-y p) s)))

(defn direction [p1 p2]
  "the angle from p1 to p2"
  (let [delta (sub p2 p1)]
    (Math/atan2 (position-y delta) (position-x delta))))

(defn dotp [p1 p2]
  "treat p1 and p2 as vectors and compute the dot product"
  (+ (* (position-x p1) (position-x p2))
     (* (position-y p1) (position-y p2))))

(defn mag2 [offset]
  "compute the magnitude squared of offset"
  (dotp offset offset))

(defn mag [offset]
  "compute the magnitude of offset"
  (Math/sqrt (mag2 offset)))

(defn normalize [p]
  "treat p like a vector and normalize"
  (scale p (/ (mag p))))

(defn project [p1 v1]
  "project p1 into direction v1"
  (let [v1n (normalize v1)]
    (scale v1n (dotp p1 (normalize v1)))))

(defn rand- [variance]
  "random value between +/- variance"
  (- (rand (* variance 2)) variance))

(defn random-position [center variance]
  "generate a random position around center with variance"
  (add center (position-absolute (rand- variance) (rand- variance))))

(defn dist2 [p1 p2]
  "compute the distance squared between 2 points"
  (mag2 (sub p1 p2)))

(defn set-position [obj position]
  "sets the position of obj (probably a mass) to position"
  (.setPosition obj (position-x position) (position-y position)))

(defn set-weapon [obj weapon & {:keys [slot]
				:or {slot 0}}]
  (.setWeapon obj weapon slot))

(defn set-engine [obj engine & {:keys [slot]
				:or {slot 0}}]
  (.setEngine obj engine slot))

(defn set-size [obj size]
  (.setSize obj size))

(defn set-faction [obj faction]
  (.setFaction obj faction))

(defn set-pilot [obj pilot]
  (.setPilot obj pilot))

(defn set-engines [obj value]
  (.setEngines obj value))

(defn destruct [obj]
  (.destruct obj))

(defn pd-controller [p d p-in d-in output]
  "returns a proportional/deriviative controller"
  (fn [dt]
    (output (+ (* p (p-in)) (* d d-in)))))

(defn engine-pd-controller [ship target-ref p d]
  "create a proportional/derivative controller to control the engine
output"
  (letfn [(p-in [] (mag (sub (position ship) (position @target-ref))))
	  (d-in [] (dotp (sub (velocity ship) (velocity @target-ref))
			 (normalize (sub (position ship)
					 (position @target-ref)))))
	  (output [val] (set-engines ship val))]
    (pd-controller p d p-in d-in output)))

(defmacro with-player
  [[symbol kind & {:keys [position]
		   :or {position (position-relative 0 0)}}] & body]
  "bind symbol to the result of evaluating kind, execute body, and
then add the result of kind to the universe as the player"

  `(let [~symbol (~kind)]
     (set-position ~symbol ~position)
     (let [result# (do ~@body)]
       (.setPlayer *universe* ~symbol)
       result#)))

(defmacro with-new
  [[symbol kind & {:keys [position]
		   :or {position (position-relative 0 0)}}] & body]
  "bind symbol to the result of evaluating kind, execute body, and
then add the result of kind to the universe"

  `(let [~symbol (~kind)]
     (set-position ~symbol ~position)
     (let [result# (do ~@body)]
       (.add *universe* ~symbol)
       result#)))

;;; functions dealing with trigger objects and their associated
;;; handlers
(defn call-with-spatial-realization
  [event-pos event-radius func]
  "internal. helper for with-spatial-realization"
  (.add *universe*
    (reify
     hypernova.Realization
     (shouldTrigger [this px py]
		    (let [player-pos (position-absolute px py)]
		      (if (<= (dist2 player-pos (position event-pos))
			      (* event-radius event-radius))
			(do
			  (.remove *universe* this)
			  true)
			 false)))

     (trigger [this px py]
	      (func (position-absolute px py))))))

(defmacro with-spatial-realization
  [[player-pos event-pos event-radius] & body]
  "execute body when player is within event-radius of
event-pos. player-pos will be bound"
  `(call-with-spatial-realization ~event-pos ~event-radius (fn [~player-pos] ~@body)))

(defn call-with-delayed-realization
  [delay func]
  "internal. helper for with-delayed-realization"
  (let [end-time (+ (System/currentTimeMillis) (* delay 1000))]
    (.add *universe*
      (reify
       hypernova.Realization
       (shouldTrigger [this px py]
		      (if (>= (System/currentTimeMillis) end-time)
			(do
			  (.remove *universe* this)
			  true)
			false))
       (trigger [this px py]
		(func (position-absolute px py)))))))

(defmacro with-delayed-realization
  [[player-pos delay] & body]
  "executes body after delay has expired. player-pos will be bound"
  `(call-with-delayed-realization ~delay (fn [~player-pos] ~@body)))

(defn repack-event-sequence [forms]
  "internal. helper for event-sequence"
  (when-not (empty? forms)
    `(~@(first forms)
      (do ~(repack-event-sequence (rest forms))))))

(defmacro event-sequence [& forms]
  "arranges for the realization events defined by forms to execute as
an ordered sequence."
  (repack-event-sequence forms))

(defprotocol Destructable
  "a thing that can be monitored for destruction"

  (add-destruct-handler
   [target func]
   "func will be called when target is destroyed"))

(defprotocol Active
  (active? [this]))

(extend Mass
  Destructable
  {:add-destruct-handler
   (fn [mass func]
     (.onDestruct mass
       (reify
	hypernova.DestructionListener
	(destroyed [this mass] (func)))))}

  Active
  {:active? (fn [mass] (.isActive mass))}

  Positional
  {:position (fn [mass] (position-absolute (.getX mass 0)
					   (.getY mass 0)))}

  Velocital
  {:velocity (fn [mass] (velocity (.getX mass 1)
				  (.getY mass 1)))})

(defrecord watch-list
  [targets handlers]

  Destructable
  (add-destruct-handler
   [this func]
   (swap! (:handlers this) conj func))

  Active
  (active?
   [this]
   (and (not-empty @(:targets this))
	(every? #'active? @(:targets this)))))

(defn make-watch-list [items]
  "create an object that can be monitored for destruction events of
the entire aggregate"
  (let [items-ref (atom (set items))
	handlers-ref (atom [])]

    ;; embed a watcher in each of our targets
    (doseq [item items]
      (add-destruct-handler item
			    (fn []
			      ;; remove the item
			      (swap! items-ref disj item)
			      ;; maybe notify our handlers
			      (when (empty? @items-ref)
				(doseq [hdlr @handlers-ref]
				  (hdlr))))))

    (watch-list. items-ref handlers-ref)))


;; required functionality that is accessed from java
(defn- get-resource [file]
  (println "getting resource " file)
  (let [loader (clojure.lang.RT/baseLoader)]
    (PushbackReader. (InputStreamReader. (.getResourceAsStream loader file)))))

(defn loader [name x y]
  (println "evaluating " name)
  (binding [*x* x
	    *y* y]
    (load-reader (get-resource name))))

(defn repl []
  (clojure.main/repl :init #(in-ns 'hypernova.activities.api)))
