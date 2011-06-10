package hypernova.pilots;

import hypernova.Ship;

public class PlayerHunter extends Pilot {
    public static final double TARGET_DIST = 60.0;

    private Ship player;

    public PlayerHunter(Ship ship) {
        super(ship);
    }

    @Override
    public void drive() {
        player = hypernova.Hypernova.universe.getPlayer();
        double x = ship.getX(0);
        double y = ship.getY(0);
        double px = player.getX(0);
        double py = player.getY(0);
        double dir = Math.atan2(py - y, px - x);
        double diff = dir - ship.getA(0);

        /* Choose the shorter direction */
        if (diff > Math.PI)
            diff -= Math.PI * 2;
        else if (diff < -Math.PI)
            diff += Math.PI * 2;

        /* Turn towards the player. */
        if (diff < 0) {
            ship.turnLeft(-diff * 10);
            ship.turnRight(false);
        } else {
            ship.turnRight(diff * 10);
            ship.turnLeft(false);
        }
        if (Math.abs(diff) < 0.05 && player.isActive()) {
            ship.fire(0);
        }

        double dist = Math.sqrt((px - x) * (px - x) + (py - y) * (py - y));
        ship.setEngines((dist - TARGET_DIST) / 250);
    }
}
