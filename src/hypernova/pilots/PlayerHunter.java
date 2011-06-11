package hypernova.pilots;

import hypernova.Ship;

public class PlayerHunter extends Hunter {
    public PlayerHunter(Ship ship) {
        super(ship, null);
    }

    private Ship getPlayer() {
        return hypernova.Hypernova.universe.getPlayer();
    }

    public void drive(double dt) {
        if (target == null || !target.isActive()) target = getPlayer();
        super.drive(dt);
    }
}
