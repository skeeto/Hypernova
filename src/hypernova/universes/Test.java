package hypernova.universes;

import hypernova.NewUniverse;
import hypernova.Activity;
import hypernova.Realization;
import hypernova.gui.Viewer;
import hypernova.activities.WarpZoneTest;
import hypernova.gui.backgrounds.EqualizerBackground;

public class Test extends NewUniverse {
   public void begin()
   {
        Viewer.setBackground(new EqualizerBackground());
        Activity battle = new hypernova.activities.FactoryBattle();
        u.addActivity(battle, -500, -500);
        WarpZoneTest w = new WarpZoneTest(new Start());
        u.addActivity(w,0,-2000);
   }
} 
