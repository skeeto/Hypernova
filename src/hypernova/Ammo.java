package hypernova;

import java.util.Properties;

import org.apache.log4j.Logger;

import hypernova.gui.Model;

public class Ammo extends Mass {
    public static final int DEFAULT_TTL = 200;
    public static final double DEFAULT_SPEED = 1.5;
    public static final double DEFAULT_DAMAGE = 1.0;
    public static final String DEFAULT_MODEL = "bolt";

    private int ttl;
    private double damage;
    private double speed;

    private static Logger log = Logger.getLogger("Ammo");

    public static Ammo getAmmo(String name) {
        String filename = "parts/" + name + ".ammo";
        log.debug("Loading ammo '" + name + "' (" + filename + ")");
        Properties props = new Properties();
        try {
            props.load(Weapon.class.getResourceAsStream(filename));
        } catch (java.io.IOException e) {
            log.error("Failed to load ammo '" + name + "': " + e.getMessage());
            /* TODO handle this more gracefully. */
            return null;
        }
        Ammo ammo = new Ammo();
        ammo.ttl = (int) Weapon.attempt(props, "ttl", DEFAULT_TTL);
        ammo.damage = Weapon.attempt(props, "damage", DEFAULT_DAMAGE);
        ammo.speed = Weapon.attempt(props, "speed", DEFAULT_SPEED);
        String modelname = props.getProperty("model");
        if (modelname == null)
            modelname = DEFAULT_MODEL;
        ammo.model = Model.getModel(modelname);
        return ammo;
    }

    protected Ammo() {
    }

    public Ammo copy(Mass src) {
        Ammo ammo = new Ammo();
        ammo.ttl = ttl;
        ammo.damage = damage;
        ammo.speed = speed;
        ammo.model = model;
        ammo.match(src, 0);
        ammo.match(src, 1);
        ammo.setA(0.0, 1);
        ammo.addX(Math.cos(src.getA(0)) * speed, 1);
        ammo.addY(Math.sin(src.getA(0)) * speed, 1);
        return ammo;
    }

    public void step(double t) {
        super.step(t);
        if (ttl-- < 0)
            Hypernova.universe.remove(this);
    }
}
