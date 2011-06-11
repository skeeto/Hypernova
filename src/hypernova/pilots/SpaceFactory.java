package hypernova.pilots;

import hypernova.Ship;
import hypernova.Universe;

public class SpaceFactory extends Pilot {
    private String tooling;
    private PilotFactory pilots;
    private double speed, timeout;

    public SpaceFactory(Ship ship, String tooling, PilotFactory pilots,
                        double speed) {
        super(ship);
        this.tooling = tooling;
        this.pilots = pilots;
        this.speed = this.timeout = speed;
    }

    @Override
    public void drive(double dt) {
        timeout -= dt;
        if (timeout > 0) return;
        timeout += speed;

        Ship forged = Ship.get(tooling);
        forged.setFaction(ship.getFaction());
        forged.setPilot(pilots.create(forged));
        forged.setPosition(ship);
        Universe.get().add(forged);
    }
}
