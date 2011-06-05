package hypernova;

import java.util.List;
import java.util.ArrayList;

public class Universe {
    private Ship player;
    private List<Ship> ships = new ArrayList<Ship>();

    public Universe() {
        /* Set up player ship. */
        player = new Ship(0, 0, Math.PI / 8);
        ships.add(player);
    }

    public List<Ship> getShips() {
        return ships;
    }

    public Ship getPlayer() {
        return player;
    }
}
