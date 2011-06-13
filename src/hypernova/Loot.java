package hypernova;

import java.awt.geom.Rectangle2D;
import java.util.Random;

public class Loot extends Mass {
    public static final int LOOT_TTL = 5000;
    public static final double SPIN_RATE = 0.1;
    public static final double DRIFT_RATE = 0.5;
    private static final Random RNG = new Random();

    private Mass cargo;
    private long gold;

    public Loot(Mass src, Mass cargo, long gold) {
        super("loot");
        shortlived = true;
        suffersdrag = true;
        ttl = LOOT_TTL;
        if (src != null) {
            setPosition(src);
            x[1] = src.x[1];
            y[1] = src.y[1];
        }
        x[1] += RNG.nextGaussian() * DRIFT_RATE;
        y[1] += RNG.nextGaussian() * DRIFT_RATE;
        a[1] = RNG.nextGaussian() * SPIN_RATE;
        this.cargo = cargo;
        this.gold = gold;
    }

    @Override
    public void step(double t) {
        x[2] = 0;
        y[2] = 0;
        super.step(t);
        Rectangle2D bounds = getHit().getBounds2D();
        Ship player = Universe.get().getPlayer();
        if (player.getHit().intersects(bounds)) {
            cashout(player);
        }
    }

    @Override
    public double getMass() {
        double mass = 0;
        if (cargo != null)
            mass = cargo.getMass();
        return super.getMass() + mass;
    }

    private void cashout(Ship s) {
        Universe u = Universe.get();
        if (cargo != null) {
            s.store(cargo);
            u.queueMessage("Picked up a " + cargo);
        } else {
            if (gold > 0) {
                u.changeGold(gold);
                u.queueMessage("Picked up " + gold + " gold.");
            } else {
                u.queueMessage("Nothing valuable here");
            }
        }
        destruct();
    }
}
