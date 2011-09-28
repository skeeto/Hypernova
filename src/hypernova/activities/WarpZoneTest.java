package hypernova.activities;

import hypernova.ActivitySimple;
import hypernova.Universe;
import hypernova.gui.Viewer;
import hypernova.gui.backgrounds.EqualizerBackground;

public class WarpZoneTest extends ActivitySimple {
    public void begin(double px, double py) {
        u.queueMessage("WARP DEBUG TEST ENCOUNTERED");
        Viewer.setBackground(new EqualizerBackground());
        Universe.get().clear();
    }

}
