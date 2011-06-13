package hypernova.activities;

import java.util.Random;

import hypernova.Mass;
import hypernova.Loot;
import hypernova.Universe;
import hypernova.Activity;

public class GoodieField extends Activity {
    public static final double SPREAD = 100.0;
    public static final int COUNT = 30;
    public static final int GOLD = 10;

    public void realize(double x, double y) {
        Random rng = new Random();
        for (int i = 0; i < COUNT; i++) {
            Mass loot = new Loot(null, null, GOLD);
            loot.setPosition(x + rng.nextGaussian() * SPREAD,
                             y + rng.nextGaussian() * SPREAD, 0);
            Universe.get().add(loot);
        }
    }
}
