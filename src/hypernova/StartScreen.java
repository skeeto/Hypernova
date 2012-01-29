package hypernova;

import java.awt.Color;
import hypernova.gui.backgrounds.EqualizerBackground;
import hypernova.gui.Viewer;
import hypernova.gui.Menu;
import hypernova.gui.menus.Intro;

public class StartScreen extends NewUniverse {
   static final long serialVersionUID = 137133L;  
   public void begin()
   {
        Viewer.setBackground(new EqualizerBackground());
        Faction.clear();
        Faction.create("Humans", Color.BLACK);
        Menu.begin(new Intro());
   }
} 
