(ns hypernova.activities.test
  (:use hypernova.activities.api))


(message "Let's go south-east and see what we find...")

(def *station-position* (position-relative 300 300))

(with-spatial-realization [player *station-position* 200]
  (message "Looks like we've found something interesting!")

  (with-new [station (mass "small-station") :position *station-position*]
    (println (str station))))
