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

    /**
     * Create a pilot that controls the given ship.
     * @param ship  the ship to be piloted.
     */
    public Pilot(Ship ship) {
        this.ship = ship;
    }

    /**
     * Called each step of iteration of the universe just before the
     * driven ship's state is updated. In this method, the ship should
     * be manipulated through the various driving commands that
     * control the level of engines and weapon firing.
     * @param dt time step
     */
    public abstract void drive(double dt);

    /**
     * Turn to face the given direction as efficiently as possible.
     * @param dt  time step
     * @param dir direction to face
     */
    protected void face(double dt, double dir) {
        double diff = dir - ship.getA(0);

        /* Choose the shorter direction */
        if (diff > Math.PI)
            diff -= Math.PI * 2;
        else if (diff < -Math.PI)
            diff += Math.PI * 2;

        double maneuv = ship.getManeuverability();
        double rate = diff / (maneuv * dt);

        if (diff < 0) {
            ship.turnLeft(-rate);
            ship.turnRight(false);
        } else {
            ship.turnRight(rate);
            ship.turnLeft(false);
        }
    }
}
