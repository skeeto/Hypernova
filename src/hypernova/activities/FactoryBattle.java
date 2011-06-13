package hypernova.activities;

import hypernova.Ship;
import hypernova.Universe;
import hypernova.pilots.SpaceFactory;
import hypernova.pilots.PilotFactory;

public class FactoryBattle extends hypernova.Activity {
    public static final double SPREAD = 250.0;
    public static final double RATE = 150.0;
    public static final double GMEAN = 10;
    public static final double GVAR = 5;
    public static final double GRATE = 0.2;

    public void realize(double x, double y) {
        createFactory("Humans", x, y, RATE / 1.5);
        for (int i = 0; i < 2; i++) {
            int dx = 1, dy = 1;
            if (i > 0 && i < 3) dx = -1;
            if (i % 2 == 0) dy = -1;
            createFactory("Invaders", SPREAD * dx + x, SPREAD * dy + y, RATE);
        }
    }

    private void createFactory(String faction, double x, double y,
                               double rate) {
        Universe u = Universe.get();
        Ship f = new Ship("factory");
        f.setFaction(faction).setPosition(x, y, 0);
        PilotFactory pf = new PilotFactory.HunterSeekerFactory();
        SpaceFactory p = new SpaceFactory(f, "drone", rate, pf);
        p.addGold(GMEAN, GVAR, GRATE);
        f.setPilot(p);
        u.add(f);
    }
}
