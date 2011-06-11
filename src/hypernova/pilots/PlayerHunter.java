package hypernova.pilots;

import hypernova.Ship;

public class PlayerHunter extends Pilot {
    public static final double TARGET_DIST = 60.0;

    private Ship player;

    public PlayerHunter(Ship ship) {
        super(ship);
    }

    @Override
    public void drive(double dt) {
        player = hypernova.Hypernova.universe.getPlayer();
        double x = ship.getX(0);
        double y = ship.getY(0);
        double px = player.getX(0);
        double py = player.getY(0);
        double dir = Math.atan2(py - y, px - x);
        face(dt, dir);

        double diff = dir - ship.getA(0);
        if (Math.abs(diff) < 0.05 && player.isActive()) {
            ship.fire(0);
        }

        double dist = Math.sqrt((px - x) * (px - x) + (py - y) * (py - y));
        ship.setEngines((dist - TARGET_DIST) / 250);
    }
}
