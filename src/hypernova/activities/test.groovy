package hypernova

import static hypernova.API.*
import hypernova.pilots.*

/* Set up player ship. */
Ship thePlayer
withNewPlayer(parts("monoship")) { player ->
    player.setPosition(0, 0, Math.PI / -2).setFaction("Humans")
    player.setWeapon("blaster", 0).setEngine("tourist", 0)
    thePlayer = player
}

boolean stationAlive = true

newSpatialRealization(300.0, 300.0, 200.0) { playerX, playerY ->
    message("Looks like we've found something interesting!\n")

    Ship theDummy
    withNew(ship("tenderfoot")) { dummy ->
        dummy.setPosition(300, 300, Math.PI / 3).setFaction("Humans")
        dummy.setWeapon("blaster", 0).setEngine("tourist", 0)
        dummy.setPilot(new hypernova.pilots.CirclePilot(dummy, 1.0))
        dummy.setSize(6.0);
        theDummy = dummy
    }

    newDelayedRealization(4) { px, py ->
        message("He's nuts... maybe the artifact...")
    }

    withNew(mass("artifact-station")) { station ->
        station.setPosition(300.0, 300.0, 0.0).setFaction("Aliens")
        station.setA(0.01, 1)
        station.setSize(30.0)
        onDestruct(station) {
           message("\$#&@ Invaders!!!\n")
           stationAlive = false
           def pilot = new HunterSeeker(theDummy)
           pilot.setTarget(thePlayer)
           theDummy.setPilot(pilot)
           newDelayedRealization(10) { px, py ->
               message("Whoah... chill out buddy. It was an accident!")
           }
           newDelayedRealization(40) { px, py ->
               message("You've got serious anger problems...")
           }
        }
    }

    newDelayedRealization(10) { newPx, newPy ->
        message("Oh no! Invaders! We need to protect this artifact!\n")

        withNew(mass("small-station")) { station ->
            station.setPosition(playerX, playerY, 0.0).setFaction("Invaders")
            station.setA(0.01, 1)
            station.setSize(30.0)
        }

        def VAR = 1000.0
        int invaderCount = 15

        (0..invaderCount).each() { idx ->
            withNew(ship("drone")) { invader ->
                invader.setWeapon("mini-blaster", 0).setEngine("microshove", 0)
                invader.setSize(3.5).setFaction("Invaders")

                def (x,y,theta) = randomPosition(VAR, playerX, playerY)
                invader.setPosition(x, y, theta)
                invader.setPilot(new hypernova.pilots.PlayerHunter(invader))

                onDestruct(invader) {
                    invaderCount = invaderCount - 1
                    if(invaderCount == 0) {
                        if(stationAlive) {
                            message("Whew! That was a close one!\n")
                        }

                        theDummy.setPilot(new EmptyCockpit())
                        newDelayedRealization(4) { px, py ->
                            message("Whew... king nutso finally chilled out...")
                        }
                        newDelayedRealization(15) { px, py ->
                            message("Well, now what?")
                        }
                    }
                }
            }
        }
    }
}

