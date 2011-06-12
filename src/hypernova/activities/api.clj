(ns hypernova.activities.api
  (:import [hypernova Universe Ship Mass Hull]
	   [java.io PushbackReader InputStreamReader]))

(defonce *universe* (Universe/get))
(def *x* 0)
(def *y* 0)

(defn parts [kind]
  #(Ship/get kind))

(defn ship [kind]
  #(.Ship kind))

(defn mass [kind]
  #(.Mass kind))

(defn message [string]
  (.queueMessage *universe* (str string)))

(defmacro with-player [[symbol kind & {:keys [x y] :or {x 0 y 0}}] & body]
  `(let [~symbol (~kind)]
     ~@body
     (.setPlayer *universe* ~symbol)))


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

