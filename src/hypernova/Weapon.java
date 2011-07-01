package hypernova;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Weapon {
    public static final double DEFAULT_COOLDOWN = 3.0;
    public static final double DEFAULT_MASS = 1.0;
    public static final String DEFAULT_AMMO = "bolt";
    public static final String DEFAULT_SOUND = "flaunch";

    public final String name, info;

    private double cooldown, timeout;
    private double mass;
    private Ammo ammo;
    private String sound = DEFAULT_SOUND;

    private static Map<String, Weapon> cache = new HashMap<String, Weapon>();
    private static Logger log = Logger.getLogger("Weapon");

    protected Weapon(String name, String info) {
        this.name = name;
        this.info = info;
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
                            props.getProperty("info"));
        weapon.cooldown = attempt(props, "cooldown", DEFAULT_COOLDOWN);
        weapon.mass = attempt(props, "mass", DEFAULT_MASS);
        String ammoname = props.getProperty("ammo");
        if (ammoname == null)
            ammoname = DEFAULT_AMMO;
        weapon.ammo = Ammo.get(ammoname);
        String sound = props.getProperty("sound");
        if (sound == null)
            sound = DEFAULT_SOUND;
        if ("null".equals(sound))
            sound = null;
        weapon.sound = sound;
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

    public void fire(Mass src) {
        if (timeout <= 0) {
            if (sound != null)
                Sound.play(sound);
            Universe.get().add(ammo.copy(src));
            timeout = cooldown;
        }
    }

    public double getMass() {
        return mass;
    }

    public void step(double t) {
        timeout -= t;
    }

    public Weapon copy() {
        Weapon weapon = new Weapon(name, info);
        weapon.sound = sound;
        weapon.ammo = ammo;
        weapon.mass = mass;
        weapon.cooldown = cooldown;
        return weapon;
    }
}
