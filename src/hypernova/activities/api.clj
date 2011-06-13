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

(defrecord position
  [x y])

(defn position-absolute [x y]
  "create a position in absolute universe coords"
  (position. x y))

(defn position-relative [x y]
  "create a position that's relative to the center we were given"
  (position-absolute (+ *x* x)
		     (+ *y* y)))

(defn position-x [p]
  (:x p))

(defn position-y [p]
  (:y p))

(defn add [p1 p2]
  "add two positions"
  (position-absolute (+ (position-x p1) (position-x p2))
		     (+ (position-y p1) (position-y p2))))

(defn sub [p1 p2]
  "subtract two positions"
  (position-absolute (- (position-x p1) (position-x p2))
		     (- (position-y p1) (position-y p2))))

(defn rand- [variance]
  "random value between +/- variance"
  (- (rand (* variance 2)) variance))

(defn random-position [center variance]
  "generate a random position around center with variance"
  (add center (position-absolute (rand- variance) (rand- variance))))

(defn dist2 [p1 p2]
  (let [offset (sub p1 p2)]
    (+ (* (position-x offset) (position-x offset))
       (* (position-y offset) (position-y offset)))))

(defn set-position [obj position]
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
  (.addRealization *universe*
    (reify
     hypernova.Realization
     (shouldTrigger [this px py]
		    (let [player-pos (position-absolute px py)]
		      (if (<= (dist2 player-pos event-pos)
			      (* event-radius event-radius))
			(do
			  (.removeRealization *universe* this)
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
    (.addRealization *universe*
      (reify
       hypernova.Realization
       (shouldTrigger [this px py]
		      (if (>= (System/currentTimeMillis) end-time)
			(do
			  (.removeRealization *universe* this)
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

(extend Mass
  Destructable
  {:add-destruct-handler
   (fn [mass func]
     (.onDestruct mass
       (reify
	hypernova.DestructionListener
	(destroyed [this mass] (func)))))})

(defrecord watch-list
  [targets handlers]

  Destructable
  (add-destruct-handler [this func] (swap! (:handlers this)
					   conj
					   func)))

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
