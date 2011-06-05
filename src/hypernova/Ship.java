package hypernova;

import java.util.List;
import java.util.ArrayList;

public class Ship extends Mass {

    private List<Weapon> weapons = new ArrayList<Weapon>();

    public Ship(Universe universe, double x, double y, double angle) {
        super(universe, x, y, angle);
    }

    public void addWeapon(Weapon w) {
        weapons.add(w);
    }

    public void fire(int n) {
        if (n >= weapons.size())
            return;
        weapons.get(n).fire(universe, getX(), getY(), getA());
    }
}
