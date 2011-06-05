package hypernova;

import java.util.List;
import java.util.ArrayList;
import java.util.Observable;

public class Universe extends Observable implements Runnable {
    public static final int SPEED = 50;

    private Mass player;
    private Thread thread = new Thread(this);

    private List<Mass> objects = new ArrayList<Mass>();

    public Universe() {
        /* Set up player ship. */
        player = new Mass(0, 0, Math.PI / 8);
        objects.add(player);
    }

    public void start() {
        thread.start();
    }

    public List<Mass> getObjects() {
        return objects;
    }

    public Mass getPlayer() {
        return player;
    }

    @Override
    public void run() {
        while (true) {
            for (Mass m : objects) m.step(1.0);
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
