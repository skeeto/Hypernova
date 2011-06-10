package hypernova.pilots;

import hypernova.Ship;

/**
 * This class is the brains behind a ship. It controls the ships
 * engines and weapons in order to meet some goal, such as attacking
 * enemy players, collecting cargo, or just following a patrol route.
 *
 * A number of methods are (will be) provided for higher-level control
 * of ship movement, because low level engine control is difficult to
 * use effictively.
 */
public abstract class Pilot {
    /** The ship being controlled by this pilot. */
    protected final Ship ship;

    public Pilot(Ship ship) {
        this.ship = ship;
    }

    /**
     * Called each step of iteration of the universe just before the
     * driven ship's state is updated. In this method, the ship should
     * be manipulated through the various driving commands that
     * control the level of engines and weapon firing.
     */
    public abstract void drive();
}
