(ns hypernova.activities.test
  (:use hypernova.activities.api))


(message "Let's go south-east and see what we find...")

(def *station-position* (position-relative 300 300))

(def artifact (atom nil))
(def invaders (atom nil))
(def crazy-guy (atom nil))

(defn station-destroyed []
  "called when the artifact station is destroyed"
  (cond
   (and @invaders (active? @invaders))
   (do
     (message "$@%# invaders!")
     (add-destruct-handler @invaders
       #(event-sequence
	 (with-delayed-realization [player 3]
	   (message "Well, so much for that mission..."))
	 (with-delayed-realization [player 4]
	   (message "Maybe it's time to reset")))))

   (active? @crazy-guy)
   (do
     (message "Hey buddy? You alright?")
     (set-pilot @crazy-guy (hypernova.pilots.PlayerHunter. @crazy-guy))
     (event-sequence
      (with-delayed-realization [player 20]
	(set-pilot @crazy-guy (hypernova.pilots.EmptyCockpit.)))
      (with-delayed-realization [player 4]
	(message "Whew. He calmed down."))
      (with-delayed-realization [player 6]
	(message "Hello? Anyone home?"))
      (with-spatial-realization [player @crazy-guy 100]
	(destruct @crazy-guy)
	(message "Uh... ooops?"))))))

(event-sequence
 (with-spatial-realization [player *station-position* 200]
   (message "Looks like we've found something interesting!")

   (with-new [station (mass "artifact-station") :position *station-position*]
     (set-faction station "Aliens")
     (add-destruct-handler station station-destroyed)
     (reset! artifact station))

   (with-new [dummy (ship "tenderfoot") :position *station-position*]
     (set-faction dummy "Humans")
     (set-weapon dummy "blaster")
     (set-engine dummy "tourist")
     (set-pilot dummy (hypernova.pilots.CirclePilot. dummy 1.0))
     (reset! crazy-guy dummy)))

 (with-delayed-realization [player 4]
   (message "He's nuts... maybe the artifiact..."))

 (with-delayed-realization [player 4]
   (message "Oh no! Invaders! We need to protect this artifact!")

   (let [invader-list
	 (make-watch-list
	  (map
	   (fn [idx]
	     (with-new [invader (parts "drone")]
	       (set-faction invader "Invaders")
	       (set-position invader (random-position player 1000.0))
	       (set-pilot invader (hypernova.pilots.PlayerHunter. invader))
	       invader))
	   (range 15)))]

     (add-destruct-handler invader-list
       #(do
	  (message "Got em all!")
	  (with-delayed-realization [player 6]
	    (when (active? @artifact)
	      (message "Now to open up that artifact...")))))

     (reset! invaders invader-list))))

