package hypernova.universes;

import hypernova.NewUniverse;
import hypernova.Activity;
import hypernova.activities.ChuckToTheFuture;
import hypernova.activities.ChuckToTheFuture2;
import hypernova.Realization;
import hypernova.SaveGame;
import hypernova.gui.Viewer;
import hypernova.gui.backgrounds.MusicStarfield;
import hypernova.activities.WarpZoneTest;

public class Start extends NewUniverse {
   public static boolean chuckDone = false;
   public static boolean chuck2Done = false;

   private static MusicStarfield ms = new MusicStarfield();

   public void begin()
   {
        SaveGame.autoSave(0, 0, SaveGame.UniName.START);
        if( chuck2Done ) MusicStarfield.bg = MusicStarfield.BackgroundType.ROTATE;
        Viewer.setBackground(ms);
        u.addActivity("test", 0, 0);

        if( !chuckDone ) u.addActivity(new ChuckToTheFuture(), 500, -500);
        else if ( !chuck2Done ) u.addActivity(new ChuckToTheFuture2(), 1500, 1500);
        
        WarpZoneTest w = new WarpZoneTest(new Test());
        u.addActivity(w,0,2000);
   }
} 
