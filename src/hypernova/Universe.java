package hypernova;

import java.util.List;
import java.util.ArrayList;
import java.util.Observable;

public class Universe extends Observable implements Runnable {
    public static final int SPEED = 50;

    private Ship player;
    private double turn = 0.0;
    private Thread thread = new Thread(this);

    private List<Ship> ships = new ArrayList<Ship>();

    public Universe() {
        /* Set up player ship. */
        player = new Ship(0, 0, Math.PI / 8);
        ships.add(player);
    }

    public void start() {
        thread.start();
    }

    public List<Ship> getShips() {
        return ships;
    }

    public Ship getPlayer() {
        return player;
    }

    @Override
    public void run() {
        while (true) {
            player.addAz(turn);
            setChanged();
            notifyObservers();
            try {
                Thread.sleep(SPEED);
            } catch (Throwable t) {
                /* We don't care, really. */
            }
        }
    }

    /* Player controls */
    public void setTurn(double val) {
        turn = val;
    }

    public void addTurn(double rate) {
        turn += rate;
    }
}
