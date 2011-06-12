package hypernova

import static hypernova.API.*

message("Let's go south-east and see what we find...")

/* Set up player ship. */
withNewPlayer(parts("monoship")) { player ->
    player.setPosition(0, 0, Math.PI / -2).setFaction("Humans")
    player.setWeapon("blaster", 0).setEngine("tourist", 0)
}

boolean stationAlive = true

newSpatialRealization(300.0, 300.0, 200.0) { playerX, playerY ->
    withNew(mass("artifact-station")) { station ->
        station.setPosition(300.0, 300.0, 0.0).setFaction("Aliens")
        station.setA(0.01, 1)
        station.setSize(30.0)
        station.onDestruct({
           message("\$#&@ Invaders!!!\n")
           stationAlive = false
        } as DestructionListener)
    }

    message("Looks like we've found something interesting!\n")
}

newSpatialRealization(300.0, 300.0, 100.0) { playerX, playerY ->
    message("Oh no! Invaders! We need to protect this artifact!\n")

    withNew(mass("small-station")) { station ->
        station.setPosition(playerX + 100, playerY + 200, 0.0).setFaction("Invaders")
        station.setA(0.01, 1)
        station.setSize(30.0)
    }

    def VAR = 500.0
    int invaderCount = 15

    (0..invaderCount).each() { idx ->
        withNew(ship("drone")) { invader ->
            invader.setWeapon("mini-blaster", 0).setEngine("microshove", 0)
            invader.setSize(3.5).setFaction("Invaders")

            def (x,y,theta) = randomPosition(VAR, playerX, playerY)
            invader.setPosition(x, y, theta)
            invader.setPilot(new hypernova.pilots.PlayerHunter(invader))

            invader.onDestruct({
                invaderCount = invaderCount - 1
                if(invaderCount == 0 && stationAlive) {
                    message("Whew! That was a close one!\n")
                }
            } as DestructionListener)
        }
    }
}
