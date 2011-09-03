package hypernova;

import java.util.Queue;
import java.util.Random;
import java.util.HashSet;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.awt.Color;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import org.apache.log4j.Logger;

import hypernova.pilots.KeyboardPilot;
import hypernova.pilots.EmptyCockpit;

public class Universe extends Observable implements Runnable {
    public static final int DELAY_MSEC = 40;
    public static final double SIM_TIMESTEP = 0.8;
    public static final Universe INSTANCE = new Universe();

    private static Logger log = Logger.getLogger("Universe");

    private static String defaultActivity = "beginner";

    private Ship player;
    private Thread thread = new Thread(this);
    private long gold = 0;

    private boolean paused;

    private Queue<String> messages = new ConcurrentLinkedQueue<String>();

    private Collection<Mass> objects = new HashSet<Mass>();
    private Queue<Mass> incoming = new ConcurrentLinkedQueue<Mass>();
    private Queue<Mass> outgoing = new ConcurrentLinkedQueue<Mass>();

    private Collection<Realization> realizations = new HashSet<Realization>();
    private Queue<Realization> inReals
    = new ConcurrentLinkedQueue<Realization>();
    private Queue<Realization> outReals
    = new ConcurrentLinkedQueue<Realization>();

    /** Cache of ship list. */
    private Collection<Mass> ships;
    private Predicate<Mass> shipfilter = new Predicate<Mass>() {
        public boolean apply(Mass m) {
            return m instanceof Ship;
        }
    };

    private Universe() {
        Faction.create("None", Color.WHITE);
        Faction.create("Humans", Color.GREEN);
        Faction.create("Aliens", new Color(0xcc, 0x00, 0xcc));
        Faction.create("Invaders", Color.RED);
    }

    public void movePlayerControlsTo(Ship newPlayer) {
        // remove the player pilot from wherever it is right now
        Ship oldShip = KeyboardPilot.get().getShip();
        if (oldShip != null) {
            oldShip.setPilot(new EmptyCockpit());
        }
        newPlayer.setPilot(KeyboardPilot.get());
        KeyboardPilot.get().setShip(newPlayer);
    }

    public void setPlayer(Ship thePlayer) {
        player = thePlayer;
        movePlayerControlsTo(player);
        add(player);
    }

    public void initialize() {
        Activity.get(defaultActivity).realize(0, 0);
    }

    public void addActivity(String name, double x, double y) {
        Activity.get(name).realize(x, y);
    }

    public void addActivity(Activity activity, double x, double y) {
        activity.realize(x, y);
    }

    public static Universe get() {
        return INSTANCE;
    }

    public static void start() {
        if (INSTANCE.thread.isAlive()) return;
        log.debug("Starting simulation thread.");
        INSTANCE.paused = false;
        INSTANCE.thread.start();
    }

    public void togglePause() {
        paused ^= true;
        log.info("Pause set to " + paused);
    }

    public void queueMessage(String msg) {
        log.info("Queued: \"" + msg + "\"");
        messages.offer(msg);
    }

    public String nextMessage() {
        return messages.poll();
    }

    public Collection<Mass> getObjects() {
        synchronized (objects) {
            return new ArrayList<Mass>(objects);
        }
    }

    public Collection<Mass> getShips() {
        synchronized (objects) {
            if (ships == null)
                ships = Collections2.filter(objects, shipfilter);
            return ships;
        }
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

    public void add(Realization r) {
        inReals.add(r);
    }

    public void remove(Realization r) {
        outReals.add(r);
    }

    public Ship getPlayer() {
        return player;
    }

    public void changeGold(long amt) {
        gold += amt;
    }

    public long getGold() {
        return gold;
    }

    private static long now() {
        return Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public void run() {
        while (true) {
            long start = now();
            if (paused) {
                sleep(start);
                continue;
            }
            synchronized (objects) {
                for (Mass m : objects)
                    m.step(SIM_TIMESTEP);
                ships = null;
                for (Realization r : realizations) {
                    if (r.shouldTrigger(player.getX(0), player.getY(0))) {
                        r.trigger(player.getX(0), player.getY(0));
                    }
                }
                Mass m;
                while ((m = outgoing.poll()) != null) {
                    objects.remove(m);
                }
                while ((m = incoming.poll()) != null ) {
                    objects.add(m);
                }
                Realization r;
                while ((r = outReals.poll()) != null) {
                    realizations.remove(r);
                }
                while ((r = inReals.poll()) != null) {
                    realizations.add(r);
                }
            }
            setChanged();
            notifyObservers();
            sleep(start);
        }
    }

    private void sleep(long start) {
        try {
            Thread.sleep(DELAY_MSEC - (now() - start));
        } catch (Throwable t) {
            /* We don't care, really. */
        }
    }
}
