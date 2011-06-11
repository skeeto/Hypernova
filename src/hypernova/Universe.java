package hypernova;

import java.util.Random;
import java.util.HashSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Observable;

import java.awt.Color;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import org.apache.log4j.Logger;

public class Universe extends Observable implements Runnable {
    public static final int SPEED = 50;

    private static Logger log = Logger.getLogger("Universe");

    private Ship player;
    private Thread thread = new Thread(this);
    private boolean paused;
    private Random rng = new Random();

    public boolean teamDamage = false;

    private Collection<Mass> objects = new HashSet<Mass>();
    private Collection<Mass> incoming = new HashSet<Mass>();
    private Collection<Mass> outgoing = new HashSet<Mass>();

    /** Cache of ship list. */
    private Collection<Mass> ships;
    private Predicate<Mass> shipfilter = new Predicate<Mass>() {
        public boolean apply(Mass m) {
            return m instanceof Ship;
        }
    };

    public Universe() {
        new Faction("None", Color.WHITE);
        new Faction("Humans", Color.GREEN);
        new Faction("Aliens", new Color(0xcc, 0x00, 0xcc));
        new Faction("Invaders", Color.RED);

        /* Set up player ship. */
        player = new Ship("tenderfoot");
        player.setPosition(0, 0, Math.PI / 3).setFaction("Humans");
        player.setWeapon("blaster", 0).setEngine("tourist", 0);
        add(player);

        Ship dummy = new Ship("tenderfoot");
        dummy.setPosition(45, 105, Math.PI / -2).setFaction("Humans");
        dummy.setPilot(new hypernova.pilots.CirclePilot(dummy, 1.0));
        dummy.setEngine("tourist", 0).setWeapon("blaster", 0);
        add(dummy);

        Mass station = new Mass("small-station");
        station.setPosition(100.0, 100.0, 0.0).setFaction("Aliens");
        station.setA(0.01, 1);
        add(station);

        double MEAN = 1000.0;
        double VAR  = 500.0;
        for (int i = 0; i < 15; i++) {
            Ship invader = new Ship("drone");
            invader.setWeapon("mini-blaster", 0).setEngine("microshove", 0);
            invader.setFaction("Invaders");
            double dirx = 1.0;
            if (rng.nextInt(2) == 0) dirx = -1.0;
            double diry = 1.0;
            if (rng.nextInt(2) == 0) diry = -1.0;
            invader.setPosition((rng.nextGaussian() * VAR + MEAN) * dirx,
                                (rng.nextGaussian() * VAR + MEAN) * diry,
                                rng.nextDouble() * Math.PI * 2);
            invader.setPilot(new hypernova.pilots.PlayerHunter(invader));
            add(invader);
        }
    }

    public void start() {
        log.debug("Starting simulation thread.");
        paused = false;
        thread.start();
    }

    public void togglePause() {
        paused ^= true;
        log.info("Pause set to " + paused);
    }

    public Collection<Mass> getObjects() {
        return objects;
    }

    public Collection<Mass> getShips() {
        if (ships == null)
            ships = Collections2.filter(objects, shipfilter);
        return ships;
    }

    public void add(Mass m) {
        m.getModel().transform(m);
        m.setActive(true);
        incoming.add(m);
    }

    public void remove(Mass m) {
        outgoing.add(m);
        m.setActive(false);
    }

    public Ship getPlayer() {
        return player;
    }

    private static long now() {
        return Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public void run() {
        while (true) {
            long start = now();
            if (!paused) {
                synchronized (objects) {
                    for (Mass m : objects) m.step(1.0);
                }
                ships = null;
                synchronized (outgoing) {
                    objects.removeAll(outgoing);
                    outgoing.clear();
                }
                synchronized (incoming) {
                    objects.addAll(incoming);
                    incoming.clear();
                }
                setChanged();
                notifyObservers();
            }
            try {
                Thread.sleep(SPEED - (now() - start));
            } catch (Throwable t) {
                /* We don't care, really. */
            }
        }
    }
}
