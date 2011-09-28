package hypernova.universes;

import hypernova.NewUniverse;
import hypernova.Activity;
import hypernova.Realization;
import hypernova.gui.Viewer;
import hypernova.gui.backgrounds.MusicStarfield;
import hypernova.activities.WarpZoneTest;

public class Start extends NewUniverse {
   public void begin()
   {
        Viewer.setBackground(new MusicStarfield());
        u.addActivity("test", 0, 0);

        Activity chuck = new hypernova.activities.ChuckToTheFuture();
        u.addActivity(chuck, 500, -500);
        
        WarpZoneTest w = new WarpZoneTest(new Test());
        u.addActivity(w,0,2000);
   }
} 
