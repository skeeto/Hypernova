package hypernova;

import java.util.List;
import java.util.ArrayList;

public class Ship extends Mass {
    public static final double DEFAULT_POWER = 0.5;
    public static final double DEFAULT_TURN = 0.15;

    private Weapon[] weapons;
    private boolean[] firestate = new boolean[0];
    private boolean engines;
    private double power = DEFAULT_POWER;
    private boolean turnleft, turnright;

    public Ship(String hullname) {
        this(Hull.getHull(hullname));
    }

    public Ship(Hull hull) {
        super(hull);
        weapons = new Weapon[hull.numWeapons()];
        firestate = new boolean[weapons.length];
    }

    public void setWeapon(String w, int slot) {
        this.setWeapon(Weapon.getWeapon(w), slot);
    }

    public void setWeapon(Weapon w, int slot) {
        weapons[slot] = w;
        firestate[slot] = false;
    }

    public void setEngines(boolean set) {
        engines = set;
    }

    public void step(double t) {
        if (engines) {
            x[2] = power * Math.cos(getA(0));
            y[2] = power * Math.sin(getA(0));
        } else {
            x[2] = 0;
            y[2] = 0;
        }
        a[1] = 0;
        if (turnleft)
            a[1] += -DEFAULT_TURN;
        if (turnright)
            a[1] += DEFAULT_TURN;

        super.step(t);
        for (int i = 0; i < weapons.length; i++) {
            weapons[i].step(t);
            if (firestate[i]) fire(i);
        }
    }

    /** Fire a single shot.
     * @param n  weapon number
     */
    public void fire(int n) {
        weapons[n].fire(this);
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
