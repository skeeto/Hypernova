package hypernova.activities;

import hypernova.ActivitySimple;

public class OneBeamer extends ActivitySimple {
    public void begin(double px, double py) {
        addShip("beamer", "Invaders", PilotType.BEAMER, px, py + 200);
    }
}
