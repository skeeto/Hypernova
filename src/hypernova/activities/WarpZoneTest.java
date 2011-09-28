package hypernova.activities;

import hypernova.ActivitySimple;
import hypernova.Universe;
import hypernova.NewUniverse;
import hypernova.gui.Viewer;
import hypernova.universes.*;
import hypernova.gui.backgrounds.EqualizerBackground;

public class WarpZoneTest extends ActivitySimple {
    private NewUniverse toLoad = new Test();

    public WarpZoneTest(NewUniverse n) {
       super();
       toLoad = n;
    }

    public void begin(double px, double py) {
        u.queueMessage("WARP DEBUG TEST ENCOUNTERED");
        u.loadUniverse(toLoad);
    }

}
