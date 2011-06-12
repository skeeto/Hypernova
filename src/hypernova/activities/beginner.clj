(ns hypernova.activities.beginner
  (:use hypernova.activities.api))

(with-player [player (parts "monoship")]
  (.setPosition player *x* *y* (/ (Math/PI) 2))
  (.setFaction player "Humans"))


