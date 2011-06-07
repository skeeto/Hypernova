package hypernova;

import java.util.List;
import java.util.ArrayList;

public class Ship extends Mass {
    public static final double DEFAULT_POWER = 0.5;
    public static final double DEFAULT_TURN = 0.15;

    private List<Weapon> weapons = new ArrayList<Weapon>();
    private boolean[] firestate = new boolean[0];
    private boolean engines;
    private double power = DEFAULT_POWER;
    private boolean turnleft, turnright;

    public Ship(double x, double y, double angle, String model) {
        super(x, y, angle, model);
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
        setA(0.0, 1);
        if (turnleft)
            addA(-DEFAULT_TURN, 1);
        if (turnright)
            addA(DEFAULT_TURN, 1);

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
        weapons.get(n).fire(this);
    }

    /** Set a weapon as currently firing or not.
     * @param n  weapon number
     */
    public void setFire(int n, boolean set) {
        firestate[n] = set;
    }

    public void turnLeft(boolean set) {
        turnleft = set;
    }

    public void turnRight(boolean set) {
        turnright = set;
    }
}
