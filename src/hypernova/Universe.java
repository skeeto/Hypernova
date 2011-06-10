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
import org.apache.log4j.Level;

import hypernova.pilots.*;

public class Universe extends Observable implements Runnable {
    public static final int SPEED = 50;
    public static final Universe INSTANCE = new Universe();

    private static Logger log = Logger.getLogger("Universe");

    private static String defaultActivity = "test.act";

    private Ship player;
    private Thread thread = new Thread(this);
    private boolean paused;
    private Random rng = new Random();

    public boolean teamDamage = false;

    private Queue<String> messages = new ConcurrentLinkedQueue<String>();

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

    private Universe() {
        new Faction("None", Color.WHITE);
        new Faction("Humans", Color.GREEN);
        new Faction("Aliens", new Color(0xcc, 0x00, 0xcc));
        new Faction("Invaders", Color.RED);
    }

    public void setPlayer(Ship thePlayer) {
	player = thePlayer;
	add(player);
    }

    public void initialize() {
	Activity test = Activity.get(defaultActivity);
	test.realize(this);
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
        messages.offer(msg);
    }

    public String nextMessage() {
        return messages.poll();
    }

    public Collection<Mass> getObjects() {
        return new ArrayList<Mass>(objects);
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
                for (Mass m : objects)
                    m.step(1.0);
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
