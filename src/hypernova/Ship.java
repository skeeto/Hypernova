package hypernova;

import java.util.List;
import java.util.ArrayList;

public class Ship extends Mass {
    private Weapon[] weapons;
    private Engine[] engines;
    private boolean enginestate;
    private boolean[] firestate = new boolean[0];
    private boolean turnleft, turnright;

    /* Derived from the above. */
    private double thrust, maneuverability;
    private double mass;

    public Ship(String hullname) {
        this(Hull.get(hullname));
    }

    public Ship(Hull hull) {
        super(hull);
        weapons = new Weapon[hull.numWeapons()];
        firestate = new boolean[weapons.length];
        engines = new Engine[hull.numEngines()];
        calc();
    }

    public Ship setWeapon(String w, int slot) {
        return this.setWeapon(Weapon.get(w), slot);
    }

    public Ship setWeapon(Weapon w, int slot) {
        weapons[slot] = w;
        firestate[slot] = false;
        return this;
    }

    public void setEngines(boolean set) {
        enginestate = set;
    }

    public Ship setEngine(String name, int slot) {
        return this.setEngine(Engine.get(name), slot);
    }

    public Ship setEngine(Engine e, int slot) {
        engines[slot] = e;
        calc();
        return this;
    }

    private void calc() {
        thrust = 0;
        maneuverability = 0;
        mass = hull.getMass();
        for (Engine e : engines) {
            if (e != null) {
                thrust += e.getThrust();
                maneuverability += e.getManeuverability();
                mass += e.getMass();
            }
        }
        for (Weapon w : weapons) {
            if (w != null) {
                mass += w.getMass();
            }
        }
    }

    public void step(double t) {
        if (enginestate) {
            x[2] = thrust / mass * Math.cos(getA(0));
            y[2] = thrust / mass * Math.sin(getA(0));
        } else {
            x[2] = 0;
            y[2] = 0;
        }
        a[1] = 0;
        if (turnleft)
            a[1] += -maneuverability;
        if (turnright)
            a[1] += maneuverability;

        super.step(t);
        for (int i = 0; i < weapons.length; i++) {
            if (weapons[i] != null) {
                weapons[i].step(t);
                if (firestate[i]) fire(i);
            }
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
