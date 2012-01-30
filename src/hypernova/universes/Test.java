package hypernova.universes;

import java.awt.Color;
import hypernova.NewUniverse;
import hypernova.Activity;
import hypernova.Faction;
import hypernova.Realization;
import hypernova.SaveGame;
import hypernova.UniNames;
import hypernova.activities.CountDown;
import hypernova.activities.OneBeamer;
import hypernova.gui.Viewer;
import hypernova.gui.Wormhole;
import hypernova.gui.Transition;
import hypernova.activities.WarpZoneTest;
import hypernova.gui.backgrounds.LineField;
import hypernova.sounds.WarbleEffect;
import hypernova.audio.MinimWrapper;

public class Test extends NewUniverse {
   public static Test INSTANCE = new Test();
   static final long serialVersionUID = 7533472837495L;

   private boolean countDone = false;
   
   private static OneBeamer oneBeamer;
   public static void finishOneBeamer(){INSTANCE.oneBeamer.finish();}
   public static void setCountDone(){INSTANCE.countDone = true;}   

   public void begin()
   {
        Faction.clear();
        Faction.create("None", Color.WHITE);
        Faction.create("Humans", Faction.ColorType.TEST_HUMAN);
        Faction.create("Invaders", Faction.ColorType.TEST_INVADER);
        SaveGame.setCheckpoint(0, 0, UniNames.TEST);
        Viewer.setBackground(new LineField());
        Activity battle = new hypernova.activities.FactoryBattle();
        u.addActivity(battle, -500, -500);
        if( !INSTANCE.countDone ) u.addActivity(new CountDown(), 500, -500);
        Wormhole.add(0,-1500,400,400,UniNames.START,Transition.Types.DIAGONAL);
        Wormhole.add(1500,0,400,400, UniNames.ALTER, Transition.Types.FOUR);
        u.queueMessage("You are there");
        oneBeamer = new hypernova.activities.OneBeamer();
        u.addActivity(oneBeamer, 0, 2000);
        MinimWrapper.addEffect(new WarbleEffect());
        WarbleEffect.l = false;
        WarbleEffect.r = false;
   }
} 
