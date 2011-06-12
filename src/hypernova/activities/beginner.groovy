package hypernova

import static hypernova.API.*

/* Set up player ship. */
Ship thePlayer
withNewPlayer(parts("monoship")) { player ->
    player.setPosition(sceneX, sceneY, Math.PI / -2).setFaction("Humans")
    thePlayer = player
}
