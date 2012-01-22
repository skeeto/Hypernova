package hypernova.universes;

import java.awt.Color;
import hypernova.NewUniverse;
import hypernova.Activity;
import hypernova.Faction;
import hypernova.gui.backgrounds.EqualizerBackground;
import hypernova.Realization;
import hypernova.SaveGame;
import hypernova.UniNames;
import hypernova.gui.Viewer;
import hypernova.gui.Info;
import hypernova.gui.Wormhole;
import hypernova.gui.Transition;
import hypernova.sounds.VolumeEffect;
import hypernova.audio.MinimWrapper;

public class Alter extends NewUniverse {
   public static Alter INSTANCE = new Alter();
   static final long serialVersionUID = 137133472837495L;  
 
   public void begin()
   {
        Faction.clear();
        Faction.create("None", Color.BLUE);
        Faction.create("Humans", Color.WHITE);
        Faction.create("Invaders", Color.WHITE);
        SaveGame.setCheckpoint(0, 0, UniNames.ALTER);
        Viewer.setBackground(new EqualizerBackground());

        Info.visibleTimer = false;
        Info.visibleCounter = false;
        
        Wormhole.add(-1500,100,400,400,UniNames.START, Transition.Types.DIAGONAL);
   }

} 
