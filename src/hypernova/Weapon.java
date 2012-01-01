package hypernova;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.awt.geom.Point2D;

import org.apache.log4j.Logger;

public class Weapon {
    public static final double DEFAULT_ENERGY = 0.0;
    public static final double DEFAULT_POWER = 0.0;
    public static final double DEFAULT_COOLDOWN = 3.0;
    public static final double DEFAULT_MASS = 1.0;
    public static final String DEFAULT_AMMO = "bolt";
    public static final String DEFAULT_TYPE = "attack";
    public static final int DEFAULT_CHANNEL = 0;
    public static final int DEFAULT_NOTE = 40;
    public static final int DEFAULT_VOLUME = 75;

    public final String name, info, type;

    private boolean isThrusting = false;
    private double cooldown, timeout, energy, power;
    private double mass;
    private Ammo ammo;
    private int channel, note, volume;

    private static Map<String, Weapon> cache = new HashMap<String, Weapon>();
    private static Logger log = Logger.getLogger("Weapon");

    protected Weapon(String name, String info, String type) {
        this.name = name;
        this.info = info;
        this.type = type;
    }

    public static Weapon get(String name) {
        Weapon weapon = cache.get(name);
        if (weapon != null) return weapon.copy();
        String filename = "parts/" + name + ".weapon";
        log.debug("Loading weapon '" + name + "' (" + filename + ")");
        Properties props = new Properties();
        try {
            props.load(Weapon.class.getResourceAsStream(filename));
        } catch (java.io.IOException e) {
            /* TODO handle this more gracefully. */
            log.error("Failed to load weapon '" + name + "': "
                      + e.getMessage());
            return null;
        }

        weapon = new Weapon(props.getProperty("name"),
                            props.getProperty("info"),
                            props.getProperty("type"));
        weapon.cooldown = attempt(props, "cooldown", DEFAULT_COOLDOWN);
        weapon.energy = attempt(props, "energy", DEFAULT_ENERGY);
        weapon.power = attempt(props, "power", DEFAULT_POWER);
        weapon.mass = attempt(props, "mass", DEFAULT_MASS);
        weapon.channel = (int) Weapon.attempt(props, "channel",
                                              DEFAULT_CHANNEL);
        weapon.note = (int) Weapon.attempt(props, "note", DEFAULT_NOTE);
        weapon.volume = (int) Weapon.attempt(props, "volume", DEFAULT_VOLUME);
        String ammoname = props.getProperty("ammo");
        if (ammoname == null)
            ammoname = DEFAULT_AMMO;
        weapon.ammo = Ammo.get(ammoname);
        cache.put(name, weapon);
        return weapon.copy();
    }

    public static double attempt(Properties ps, String prop, double def) {
        String str = ps.getProperty(prop);
        if (str == null)
            return def;
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            log.warn("Invalid property value for '" + prop + "'");
            return def;
        }
    }

    public void fire(Mass src, Point2D.Double p, boolean fstate, int n) {
        Ship s = null;
        if( src instanceof Ship ) s = (Ship) src;

        if (timeout <= 0 && fstate && src.useEnergy(energy)) {
            // Sound.play("fire");
            if("thruster".equals(type) && !isThrusting && s != null){
                s.setThrustMod(power);
                s.setEngines(true);
                isThrusting = true;
            } else if("fullstop".equals(type)) {
                s.setX(0,1);
                s.setY(0,1);
            } else if ("attack".equals(type)) {
              Universe.get().add(ammo.copy(src,p));
            } else if ("dual".equals(type) && s != null) {
              s.fire( n + 1 );
              s.fire( n + 2 );
            }
            timeout = cooldown;
        } else if("thruster".equals(type) && isThrusting && s != null) {
            s.setThrustMod(-power);
            s.setEngines(true);
            isThrusting = false;
        }
    }

    public double getMass() {
        return mass;
    }

    public void step(double t) {
        timeout -= t;
    }

    public Weapon copy() {
        Weapon weapon = new Weapon(name, info, type);
        weapon.ammo = ammo;
        weapon.isThrusting = isThrusting;
        weapon.mass = mass;
        weapon.cooldown = cooldown;
        weapon.energy = energy;
        weapon.power = power;
        weapon.channel = channel;
        weapon.note = note;
        weapon.volume = volume;
        return weapon;
    }
}
