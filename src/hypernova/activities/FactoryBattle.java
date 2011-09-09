package hypernova.activities;

import java.util.Random;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import hypernova.Ship;
import hypernova.Universe;
import hypernova.Activity;
import hypernova.gui.MapMarker;
import hypernova.Realization;
import hypernova.pilots.SpaceFactory;
import hypernova.pilots.PilotFactory;

public class FactoryBattle extends Activity implements Realization {
    private static final Random RNG = new Random();
    public static final double SPREAD = 250.0;
    public static final double RATE = 150.0;
    public static final int SUPPORT = 20;
    public static final int EXTRAS = 3;

    private double x, y;
    private Shape zone;

    public void realize(double x, double y) {
        this.x = x;
        this.y = y;
        MapMarker.add(new MapMarker(x,y,0,255,0));
        zone = new Rectangle2D.Double(x - SPREAD, y - SPREAD,
                                      SPREAD * 2, SPREAD * 2);
        Universe.get().add(this);
    }

    public boolean shouldTrigger(double x, double y) {
        return zone.contains(x, y);
    }

    public void trigger(double px, double py) {
        Universe.get().remove(this);
        Universe.get().queueMessage("These automated factories are battling!");
        createFactory("Humans", x, y, RATE / 1.5, false);
        for (int i = 0; i < 2; i++) {
            int dx = 1, dy = 1;
            if (i > 0 && i < 3) dx = -1;
            if (i % 2 == 0) dy = -1;
            createFactory("Invaders", SPREAD * dx + x,
                          SPREAD * dy + y, RATE, true);
        }
    }

    private void createFactory(String faction, double x, double y,
                               double rate, boolean extras) {
        Universe u = Universe.get();
        Ship f = new Ship("factory");
        f.setCanMove(false);
        f.setFaction(faction).setPosition(x, y, 0);
        PilotFactory pf = new PilotFactory.HunterSeekerFactory();
        SpaceFactory p = new SpaceFactory(f, "drone", rate, SUPPORT, pf);
        f.setPilot(p);
        u.add(f);
        if (extras) {
            for (int i = 0; i < EXTRAS; i++) {
                Ship s = Ship.get("drone");
                s.setPilot(pf.create(s));
                s.setFaction(faction);
                s.setPosition(x + RNG.nextGaussian() * SPREAD,
                              y + RNG.nextGaussian() * SPREAD, 0);
                u.add(s);
            }
        }
    }
}
