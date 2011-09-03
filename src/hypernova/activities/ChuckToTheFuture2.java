package hypernova.activities;

import hypernova.ActivitySimple;
import hypernova.MinimWrapper;
import hypernova.gui.Viewer;

public class ChuckToTheFuture2 extends ActivitySimple {
    public void begin(double px, double py) {
        Viewer.setClearScreen(false);
        addShip("beamer", "Invaders", PilotType.BEAMER, px + 200, py + 200);
        addShip("drone", "Invaders", PilotType.PLAYER_HUNTER, px + 100, py - 100);
        
        MinimWrapper.playSoundAsync("sounds/chuckToTheFuture.mp3");
        u.queueMessage("CHUCK TO THE FUTURE");
        u.queueMessage("PART 2 (3D)");
    }

}
