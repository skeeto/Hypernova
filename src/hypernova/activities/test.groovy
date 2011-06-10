package hypernova
import static hypernova.API.*

print "hello from groovy!\n"

/* Set up player ship. */
withNewPlayer("tenderfoot") { player ->
    player.setPosition(0, 0, Math.PI / -2).setFaction("Humans")
    player.setWeapon("blaster", 0).setEngine("tourist", 0)
}

withNewShip("tenderfoot") { dummy ->
    dummy.setPosition(45, 105, Math.PI / 3).setFaction("Humans");
    dummy.setWeapon("blaster", 0);
    dummy.setSize(6.0);
}

withNewMass("small-station") { station ->
    station.setPosition(100.0, 100.0, 0.0).setFaction("Aliens");
    station.setA(0.01, 1);
    station.setSize(30.0);
}

