package hypernova.universes;

import java.awt.Color;
import hypernova.NewUniverse;
import hypernova.Activity;
import hypernova.Faction;
import hypernova.activities.ChuckToTheFuture;
import hypernova.activities.ChuckToTheFuture2;
import hypernova.Realization;
import hypernova.SaveGame;
import hypernova.UniNames;
import hypernova.gui.Viewer;
import hypernova.gui.Info;
import hypernova.gui.Wormhole;
import hypernova.gui.Transition;
import hypernova.gui.backgrounds.MusicStarfield;
import hypernova.activities.WarpZoneTest;
import hypernova.sounds.VolumeEffect;
import hypernova.audio.MinimWrapper;

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
        Faction.clear();
        Faction.create("None", Color.WHITE);
        Faction.create("Humans", Color.GREEN);
        Faction.create("Aliens", new Color(0xcc, 0x00, 0xcc));
        Faction.create("Invaders", Color.RED);

        Info.visibleTimer = false;
        Info.visibleCounter = false;
        
        MusicStarfield.setClearScreen(true);
        if(INSTANCE == null) INSTANCE = new Start();
        SaveGame.setCheckpoint(0, 0, UniNames.START);
        if(INSTANCE.chuck2Done) MusicStarfield.bg = MusicStarfield.BackgroundType.ROTATE;
        Viewer.setBackground(ms);
        u.addActivity("test", 0, 0);

        if( !INSTANCE.chuckDone ) u.addActivity(new ChuckToTheFuture(), 500, -500);
        else if ( !INSTANCE.chuck2Done ) u.addActivity(ChuckToTheFuture.chuck2, 1500, 1500);
        
        Wormhole.add(0,1500,400,400,UniNames.TEST, Transition.Types.BLOCKING);
        Wormhole.add(-1500,0,400,400, UniNames.ALTER, Transition.Types.FOUR);
        u.queueMessage("You are here");
        MinimWrapper.addEffect(new VolumeEffect());
   }

} 
