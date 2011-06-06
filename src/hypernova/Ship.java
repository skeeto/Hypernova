package hypernova;

import java.util.List;
import java.util.ArrayList;

public class Ship extends Mass {
    public static final double DEFAULT_POWER = 0.1;

    private List<Weapon> weapons = new ArrayList<Weapon>();
    private boolean[] firestate = new boolean[0];
    private boolean engines;
    private double power = DEFAULT_POWER;

    public Ship(Universe universe, double x, double y, double angle,
                String model) {
        super(universe, x, y, angle, model);
    }

    public void addWeapon(Weapon w) {
        weapons.add(w);
        firestate = new boolean[weapons.size()];
    }

    public void setEngines(boolean set) {
        engines = set;
    }

    public void step(double t) {
        if (engines) {
            setX(power * Math.cos(getA(0)), 2);
            setY(power * Math.sin(getA(0)), 2);
        } else {
            setX(0, 2);
            setY(0, 2);
        }
        super.step(t);
        for (int i = 0; i < weapons.size(); i++) {
            weapons.get(i).step(t);
            if (firestate[i]) fire(i);
        }
    }

    /** Fire a single shot.
     * @param n  weapon number
     */
    public void fire(int n) {
        if (n >= weapons.size())
            return;
        weapons.get(n).fire(universe, getX(0), getY(0), getA(0));
    }

    /** Set a weapon as currently firing or not.
     * @param n  weapon number
     */
    public void setFire(int n, boolean set) {
        firestate[n] = set;
        fire(n);
    }
}
