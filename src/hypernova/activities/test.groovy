package hypernova

import static hypernova.API.*

print("hello from groovy!\n")

/* Set up player ship. */
withNewPlayer("tenderfoot") { player ->
    player.setPosition(0, 0, Math.PI / -2).setFaction("Humans")
    player.setWeapon("blaster", 0).setEngine("tourist", 0)
}

withNewShip("tenderfoot") { dummy ->
    dummy.setPosition(45, 105, Math.PI / 3).setFaction("Humans")
    dummy.setWeapon("blaster", 0).setEngine("tourist", 0)
    dummy.setPilot(new hypernova.pilots.CirclePilot(dummy, 1.0))
    dummy.setSize(6.0);
}

withNewMass("small-station") { station ->
    station.setPosition(300.0, 300.0, 0.0).setFaction("Aliens")
    station.setA(0.01, 1)
    station.setSize(30.0)
}

newSpatialRealization(300.0, 300.0, 100.0) { playerX, playerY ->
    print("Invaders!\n")

    withNewMass("small-station") { station ->
        station.setPosition(playerX + 100, playerY + 200, 0.0).setFaction("Invaders")
        station.setA(0.01, 1)
        station.setSize(30.0)
    }

    def VAR = 500.0

    (0..15).each() { idx ->
        withNewShip("drone") { invader ->
            invader.setWeapon("mini-blaster", 0).setEngine("microshove", 0)
            invader.setSize(3.5).setFaction("Invaders")

            def (x,y,theta) = randomPosition(VAR, playerX, playerY)
            invader.setPosition(x, y, theta)
            invader.setPilot(new hypernova.pilots.PlayerHunter(invader))
        }
    }
}
