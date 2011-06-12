(ns hypernova.activities.api
  (:import [hypernova Universe Ship Mass Hull]
	   [java.io PushbackReader InputStreamReader]))

(defonce *universe* (Universe/get))
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
  (position-absolute (+ (position-x p1) (position-x p2))
		     (+ (position-y p1) (position-y p2))))

(defn sub [p1 p2]
  (position-absolute (- (position-x p1) (position-x p2))
		     (- (position-y p1) (position-y p2))))

(defn dist2 [p1 p2]
  (let [offset (sub p1 p2)]
    (+ (* (position-x offset) (position-x offset))
       (* (position-y offset) (position-y offset)))))

(defn set-position [obj position]
  (.setPosition obj (position-x position) (position-y position)))

(defmacro with-player [[symbol kind & {:keys [position]
				       :or {position (position-relative 0 0)}}] & body]
  `(let [~symbol (~kind)]
     (set-position ~symbol ~position)
     ~@body
     (.setPlayer *universe* ~symbol)))

(defmacro with-new [[symbol kind & {:keys [position]
				    :or {position (position-relative 0 0)}}] & body]
  `(let [~symbol (~kind)]
     (set-position ~symbol ~position)
     ~@body
     (.add *universe* ~symbol)))

(defn call-with-spatial-realization [event-pos event-radius func]
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
	      (let [player-pos (position-absolute px py)]
		(func player-pos))))))

(defmacro with-spatial-realization [[player-pos event-pos event-radius] & body]
  `(call-with-spatial-realization ~event-pos ~event-radius (fn [~player-pos] ~@body)))

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
