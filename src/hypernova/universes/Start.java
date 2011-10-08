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
   public static Start INSTANCE = new Start();
   static final long serialVersionUID = 137533472837495L;  
 
   private boolean chuckDone = false;
   private boolean chuck2Done = false;

   private static MusicStarfield ms = new MusicStarfield();

   public static boolean isChuckDone()   { return INSTANCE.chuckDone; }
   public static void setChuckDone()  { INSTANCE.chuckDone = true;  SaveGame.autosave(); }
   public static void setChuck2Done() { INSTANCE.chuck2Done = true; SaveGame.autosave(); }   

   public void begin()
   {
        MusicStarfield.setClearScreen(true);
        if(INSTANCE == null) INSTANCE = new Start();
        SaveGame.setCheckpoint(0, 0, SaveGame.UniName.START);
        if(INSTANCE.chuck2Done) MusicStarfield.bg = MusicStarfield.BackgroundType.ROTATE;
        Viewer.setBackground(ms);
        u.addActivity("test", 0, 0);

        if( !INSTANCE.chuckDone ) u.addActivity(new ChuckToTheFuture(), 500, -500);
        else if ( !INSTANCE.chuck2Done ) u.addActivity(ChuckToTheFuture.chuck2, 1500, 1500);
        
        WarpZoneTest w = new WarpZoneTest(new Test());
        u.addActivity(w,0,2000);
   }

} 
