package hypernova;

import java.util.List;
import java.util.Vector;
import java.util.Observable;

import org.apache.log4j.Logger;

public class Universe extends Observable implements Runnable {
    public static final int SPEED = 50;

    private static Logger log = Logger.getLogger("Universe");

    private Ship player;
    private Thread thread = new Thread(this);

    private List<Mass> objects = new Vector<Mass>();
    private List<Mass> incoming = new Vector<Mass>();
    private List<Mass> outgoing = new Vector<Mass>();

    public Universe() {
        /* Set up player ship. */
        player = new Ship("tenderfoot");
        player.setPosition(0, 0, Math.PI / -2);
        player.setWeapon("blaster", 0);
        objects.add(player);

        Ship dummy = new Ship("tenderfoot");
        dummy.setPosition(45, 105, Math.PI / 3);
        dummy.setWeapon("blaster", 0);
        dummy.setSize(6.0);
        objects.add(dummy);

        Mass station = new Mass("small-station").setPosition(100.0, 100.0, 0.0);
        station.setA(0.01, 1);
        station.setSize(30.0);
        objects.add(station);
    }

    public void start() {
        log.debug("Starting simulation thread.");
        thread.start();
    }

    public List<Mass> getObjects() {
        return objects;
    }

    public void add(Mass m) {
        incoming.add(m);
    }

    public void remove(Mass m) {
        outgoing.add(m);
    }

    public Ship getPlayer() {
        return player;
    }

    @Override
    public void run() {
        while (true) {
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
            try {
                Thread.sleep(SPEED);
            } catch (Throwable t) {
                /* We don't care, really. */
            }
        }
    }
}
