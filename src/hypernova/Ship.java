package hypernova;

import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;

import hypernova.pilots.Pilot;
import hypernova.pilots.EmptyCockpit;

public class Ship extends Mass {
    public static final double BACK_LIMIT = -0.5;

    private static Map<String, Ship> cache = new HashMap<String, Ship>();
    private static Logger log = Logger.getLogger("Ship");
    private static final Random RNG = new Random();

    public String name, info, collision;

    private boolean canMove  = true;
    private Weapon[] weapons;
    private Engine[] engines;
    private double enginestate;
    private boolean[] firestate = new boolean[0];
    private double turnleft, turnright;
    private Pilot pilot = new EmptyCockpit();
    private Collection<Mass> hold = new HashSet<Mass>();
    private long gold;

    private double goldrate, goldmean, goldvar, collAmount;

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

    public static Ship get(String name) {
        Ship ship = cache.get(name);
        if (ship != null) return ship.copy();
        String filename = "parts/" + name + ".ship";
        log.debug("Loading ship '" + name + "' (" + filename + ")");
        Properties props = new Properties();
        try {
            props.load(Ship.class.getResourceAsStream(filename));
        } catch (Exception e) {
            /* TODO handle this more gracefully. */
            log.error("Failed to load ship '" + name + "': " + e);
            return null;
        }

        ship = new Ship(props.getProperty("hull"));
        ship.name = props.getProperty("name");
        ship.info = props.getProperty("info");
        ship.collision = props.getProperty("collision");
        String weapons = props.getProperty("weapons");
        if (weapons != null) {
            int i = 0;
            for (String w : weapons.split("\\s+")) {
                ship.setWeapon(w, i++);
            }
        }
        String engines = props.getProperty("engines");
        if (engines != null) {
            int i = 0;
            for (String e : engines.split("\\s+")) {
                ship.setEngine(e, i++);
            }
        }
        ship.goldrate = Weapon.attempt(props, "goldrate", 0);
        ship.goldmean = Weapon.attempt(props, "goldmean", 0);
        ship.goldvar = Weapon.attempt(props, "goldvar", 0);
        ship.collAmount = Weapon.attempt(props, "collamount", 0);
        cache.put(name, ship);
        return ship.copy();
    }
 
    public void collision(Mass m) {
        if("damage".equals(collision) && m.getFaction() != this.getFaction()){
            Collision.hit(m,this,collAmount,false);
            Collision.bounce(this);
        } else {
            Collision.bounce(this);
        }
    }

    public Ship copy() {
        Ship copy = new Ship(hull.copy());
        copy.name = name;
        copy.info = info;
        copy.collision = collision;
        for (int i = 0; i < weapons.length; i++)
            copy.setWeapon(weapons[i].copy(), i);
        for (int i = 0; i < engines.length; i++)
            copy.setEngine(engines[i], i);
        copy.goldrate = goldrate;
        copy.goldmean = goldmean;
        copy.collAmount = collAmount;
        copy.goldvar = goldvar;
        if (RNG.nextDouble() < goldrate) {
            double r = RNG.nextGaussian();
            copy.gold = (long) Math.max(0, r * goldvar + goldmean);
        }
        return copy;
    }

    public void setCanMove(boolean moves) {
        canMove = moves;
    }

    public Ship setWeapon(String w, int slot) {
        return this.setWeapon(Weapon.get(w), slot);
    }

    public Ship setWeapon(Weapon w, int slot) {
        weapons[slot] = w;
        firestate[slot] = false;
        return this;
    }

    public Ship setEngines(boolean set) {
        enginestate = set ? thrust : 0.0;
        return this;
    }

    public Ship setEngines(double rate) {
        enginestate = Math.max(Math.min(rate, 1.0), BACK_LIMIT) * thrust;
        return this;
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

    public void destruct() {
        Universe u = Universe.get();
        for (Mass m : hold) {
            u.add(new Loot(this, m, 0));
        }
        if (gold > 0)
            u.add(new Loot(this, null, gold));
        super.destruct();
    }

    public void setThrustMod(double val)
    {
        thrust += val;
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
        for (Mass m : hold) {
            if (m != null) {
                mass += m.getMass();
            }
        }
    }

    public void step(double t) {
        pilot.drive(t);
        x[2] = enginestate / mass * Math.cos(getA(0));
        y[2] = enginestate / mass * Math.sin(getA(0));
        a[1] = 0;
        a[1] += -turnleft;
        a[1] += turnright;

        super.step(t);
        for (int i = 0; i < weapons.length; i++) {
            if (weapons[i] != null) {
                weapons[i].step(t);
                if(hull.getSlot(i) != null) fire(i, firestate[i]);
            }
        }
    }

    /** Fire a single shot.
     * @param n  weapon number
     */
    public void fire(int n) {
        fire(n, true);
    }

    /** Fire a single shot.
     * @param n  weapon number
     * @param fstate weather the weapon is firing
     */
    public void fire(int n, boolean fstate) {
        Weapon w = weapons[n];
        if (w != null) w.fire(this, hull.getSlot(n), fstate, n);
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

    public boolean getCanMove() {
        return canMove;
    }

    public double getManeuverability() {
        return maneuverability;
    }

    public double getThrust() {
        System.out.println("thrust: " + thrust);
        return thrust;
    }

    public Collection<Mass> getHold() {
        return hold;
    }

    public void store(Mass m) {
        hold.add(m);
        calc();
    }

    public void store(long gold) {
        this.gold += gold;
    }

    public void unstore(Mass m) {
        hold.remove(m);
        calc();
    }
}
