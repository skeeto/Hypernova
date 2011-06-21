package hypernova.pilots;

import hypernova.Mass;
import hypernova.Ship;
import hypernova.Faction;
import hypernova.Universe;

public class HunterSeeker extends Hunter {
    public HunterSeeker(Ship ship) {
        super(ship, null);
    }

    @Override
    public void drive(double dt) {
        if (target == null || !target.isActive()) {
            Ship ship = getShip();

            Mass old = target;
            target = null;
            Faction faction = ship.getFaction();
            double tdist = 0;
            for (Mass m : Universe.get().getShips()) {
                if (m.getFaction() == faction) continue;
                if (target == null) {
                    target = m;
                    tdist = distance2(m);
                    continue;
                }
                double dist = distance2(m);
                if (dist < tdist) {
                    target = m;
                    tdist = dist;
                }
            }
            if (target == null)
                target = old;
        }
        super.drive(dt);
    }

    private double distance2(Mass m) {
        Ship ship = getShip();

        double x = m.getX(0) - ship.getX(0);
        double y = m.getY(0) - ship.getY(0);
        return x * x + y * y;
    }
}
