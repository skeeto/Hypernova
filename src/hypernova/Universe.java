package hypernova;

import java.util.List;
import java.util.Vector;
import java.util.Observable;

public class Universe extends Observable implements Runnable {
    public static final int SPEED = 50;

    private Ship player;
    private Thread thread = new Thread(this);

    private List<Mass> objects = new Vector<Mass>();
    private List<Mass> incoming = new Vector<Mass>();
    private List<Mass> outgoing = new Vector<Mass>();

    public Universe() {
        /* Set up player ship. */
        player = new Ship(this, 0, 0, Math.PI / -2);
        player.addWeapon(new Weapon(20.0, 2.0));
        objects.add(player);
        objects.add(new Mass(this, 20.0, 20.0, 0.0));
    }

    public void start() {
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
