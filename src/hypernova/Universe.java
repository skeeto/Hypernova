package hypernova;

import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.Calendar;
import java.util.Observable;

import java.awt.Color;

import org.apache.log4j.Logger;

public class Universe extends Observable implements Runnable {
    public static final int SPEED = 50;

    private static Logger log = Logger.getLogger("Universe");

    private Ship player;
    private Thread thread = new Thread(this);
    private boolean paused;
    private Random rng = new Random();

    public boolean teamDamage = false;

    private List<Mass> objects = new Vector<Mass>();
    private List<Mass> incoming = new Vector<Mass>();
    private List<Mass> outgoing = new Vector<Mass>();

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

    public List<Mass> getObjects() {
        return objects;
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
