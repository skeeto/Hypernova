package hypernova.universes;

import hypernova.NewUniverse;
import hypernova.Activity;
import hypernova.Realization;
import hypernova.SaveGame;
import hypernova.UniNames;
import hypernova.gui.Viewer;
import hypernova.gui.Wormhole;
import hypernova.gui.Transition;
import hypernova.activities.WarpZoneTest;
import hypernova.gui.backgrounds.EqualizerBackground;

public class Test extends NewUniverse {
   public static Test INSTANCE = new Test();
   static final long serialVersionUID = 7533472837495L;  
   public void begin()
   {
        SaveGame.setCheckpoint(0, 0, UniNames.TEST);
        Viewer.setBackground(new EqualizerBackground());
        Activity battle = new hypernova.activities.FactoryBattle();
        u.addActivity(battle, -500, -500);
        Wormhole.add(0,-1500,400,400,UniNames.START,Transition.Types.DIAGONAL);
        u.queueMessage("You are there");
   }
} 
