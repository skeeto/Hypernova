package hypernova.pilots;

import java.util.Random;

import hypernova.Ship;
import hypernova.Universe;

public class SpaceFactory extends Pilot {
    private final Random RNG = new Random();
    private String tooling;
    private PilotFactory pilots;
    private double speed, timeout;
    private double meanGold;
    private double varGold;
    private double dropRate;

    public SpaceFactory(Ship ship, String tooling, double speed,
                        PilotFactory pilots) {
        super(ship);
        this.tooling = tooling;
        this.pilots = pilots;
        this.speed = this.timeout = speed;
    }

    public void addGold(double mean, double var, double rate) {
        meanGold = mean;
        varGold = var;
        dropRate = rate;
    }

    @Override
    public void drive(double dt) {
        timeout -= dt;
        if (timeout > 0) return;
        timeout += speed;

	Ship ship = getShip();
        Ship forged = Ship.get(tooling);
        forged.setFaction(ship.getFaction());
        forged.setPilot(pilots.create(forged));
        forged.setPosition(ship);
        if (RNG.nextDouble() < dropRate) {
            long gold = (long) (RNG.nextGaussian() * varGold + meanGold);
            if (gold > 0) forged.store(gold);
        }
        Universe.get().add(forged);
    }
}
