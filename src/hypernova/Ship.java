package hypernova;

import java.util.List;
import java.util.ArrayList;

import hypernova.pilots.Pilot;
import hypernova.pilots.EmptyCockpit;

public class Ship extends Mass {
    public static final double BACK_LIMIT = -0.5;

    private Weapon[] weapons;
    private Engine[] engines;
    private double enginestate;
    private boolean[] firestate = new boolean[0];
    private double turnleft, turnright;
    private Pilot pilot = new EmptyCockpit();

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
        enginestate = set ? thrust : 0.0;
    }

    public void setEngines(double rate) {
        enginestate = Math.max(Math.min(rate, 1.0), BACK_LIMIT) * thrust;
    }

    public Ship setEngine(String name, int slot) {
        return this.setEngine(Engine.get(name), slot);
    }

    public Ship setEngine(Engine e, int slot) {
        engines[slot] = e;
        calc();
        return this;
    }

    public Ship setPilot(Pilot pilot) {
        if (pilot != null)
            this.pilot = pilot;
        else
            pilot = new EmptyCockpit();
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
        pilot.drive(t);
        x[2] = enginestate / mass * Math.cos(getA(0));
        y[2] = enginestate / mass * Math.sin(getA(0));
        a[1] = 0;
        a[1] += -turnleft * t;
        a[1] += turnright * t;

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
        Weapon w = weapons[n];
        if (w != null) w.fire(this);
    }

    /** Set a weapon as currently firing or not.
     * @param n  weapon number
     */
    public void setFire(int n, boolean set) {
        firestate[n] = set;
    }

    public void turnLeft(boolean set) {
        turnleft = set ? maneuverability : 0.0;
    }

    public void turnLeft(double set) {
        turnleft = Math.max(Math.min(set, 1.0), 0.0) * maneuverability;
    }

    public void turnRight(boolean set) {
        turnright = set ? maneuverability : 0.0;
    }

    public void turnRight(double set) {
        turnright = Math.max(Math.min(set, 1.0), 0.0) * maneuverability;
    }

    public double getMass() {
        return mass;
    }

    public double getManeuverability() {
        return maneuverability;
    }

    public double getThrust() {
        return thrust;
    }
}
