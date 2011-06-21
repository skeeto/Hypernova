package hypernova.pilots;

import hypernova.Ship;

public class CirclePilot extends Pilot {
    protected double size;
    private int firecount;

    public CirclePilot(Ship ship, double size) {
        super(ship);
        this.size = size;
    }

    @Override
    public void drive(double dt) {
        Ship ship = getShip();
        ship.turnLeft(size);
        ship.setEngines(true);
        if (firecount++ % 20 == 0)
            ship.fire(0);
    }
}
