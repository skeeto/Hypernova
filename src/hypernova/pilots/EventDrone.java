package hypernova.pilots;

import hypernova.Mass;
import hypernova.Ship;
import hypernova.Universe;;
import hypernova.ActivitySimple;

public class EventDrone extends EventPilot {
    public static final double TARGET_DIST = 60.0;

    protected Mass target = Universe.get().getPlayer();

    public EventDrone (Ship ship, ActivitySimple listener, int event)
    {
      super(ship,listener,event);
    }

    public void setTarget(Mass target) 
    {
        this.target = target;
    }

    public void fireShots(Ship ship)
    {
      ship.fire(0);
    }

    @Override
    public void drive(double dt) {
        if (target == null) return;

        Ship ship = getShip();
        double x = ship.getX(0);
        double y = ship.getY(0);
        double px = target.getX(0);
        double py = target.getY(0);
        double dir = Math.atan2(py - y, px - x);
        face(dt, dir);

        double diff = dir - ship.getA(0);
        if (Math.abs(diff) < 0.05 && target.isActive()) fireShots(ship);

        double dist = Math.sqrt((px - x) * (px - x) + (py - y) * (py - y));
        ship.setEngines((dist - TARGET_DIST) / 250);
    }
}
