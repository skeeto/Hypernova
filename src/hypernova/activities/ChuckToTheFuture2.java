package hypernova.activities;

import hypernova.ActivitySimple;
import hypernova.audio.MinimWrapper;
import hypernova.sounds.ChopEffect;
import hypernova.gui.Viewer;
import hypernova.gui.backgrounds.MusicStarfield;

public class ChuckToTheFuture2 extends ActivitySimple {
    private static int eTicket = 0;
    public void begin(double px, double py) {
        MusicStarfield.setClearScreen(false);
        eTicket = MinimWrapper.addEffect(new ChopEffect());
        System.out.println(eTicket);
        addShip("beamer", "Invaders", PilotType.BEAMER, px + 200, py + 200);
        addShip("drone", "Invaders", PilotType.PLAYER_HUNTER, px + 100, py - 100);
        
        MinimWrapper.playSoundAsync("sounds/chuckToTheFuture.mp3");
        u.queueMessage("CHUCK TO THE FUTURE -- Part 2");
    }

    public void finish() {
      super.finish();
      MinimWrapper.removeEffect(eTicket);
    }
}
