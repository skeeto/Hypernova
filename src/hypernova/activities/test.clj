(ns hypernova.activities.test
  (:use hypernova.activities.api))


(message "Let's go south-east and see what we find...")

(def *station-position* (position-relative 300 300))

(event-sequence
 (with-spatial-realization [player *station-position* 200]
   (message "Looks like we've found something interesting!")

   (with-new [station (mass "small-station") :position *station-position*]
     (set-faction station "Aliens"))

   (with-new [dummy (ship "tenderfoot") :position *station-position*]
     (set-faction dummy "Humans")
     (set-weapon dummy "blaster")
     (set-engine dummy "tourist")
     (set-pilot dummy (hypernova.pilots.CirclePilot. dummy 1.0))))

 (with-delayed-realization [player 4]
   (message "He's nuts... maybe the artifiact..."))

 (with-delayed-realization [player 4]
   (message "Oh no! Invaders! We need to protect this artifact!")

   (dotimes [idx 15]
     (with-new [invader (ship "drone")]
       (set-weapon invader "mini-blaster")
       (set-engine invader "microshove")
       (set-size invader 3.5)
       (set-faction invader "Invaders")
       (set-position invader (random-position player 1000.0))
       (set-pilot invader (hypernova.pilots.PlayerHunter. invader))))))
