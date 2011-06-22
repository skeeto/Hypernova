package hypernova;

import java.util.Properties;

import org.apache.log4j.Logger;

import hypernova.gui.Model;

public class Ammo extends Mass {
    public static final int DEFAULT_TTL = 200;
    public static final double DEFAULT_SPEED = 1.5;
    public static final double DEFAULT_DAMAGE = 1.0;
    public static final String DEFAULT_MODEL = "bolt";
    public static final double HIT_DIVISION = 8.0;

    public final String name, info;

    private double damage;
    private double speed;
    private Mass source;

    private static Logger log = Logger.getLogger("Ammo");

    private Ammo(Hull hull, String name, String info) {
        super(hull);
        this.name = name;
        this.info = info;
    }

    public static Ammo get(String name) {
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

        String hullname = props.getProperty("hull");
        if (hullname == null)
            hullname = DEFAULT_MODEL;
        Hull hull = Hull.get(hullname);
        Ammo ammo = new Ammo(hull, props.getProperty("name"),
                             props.getProperty("info"));
        ammo.ttl = (int) Weapon.attempt(props, "ttl", DEFAULT_TTL);
        ammo.damage = Weapon.attempt(props, "damage", DEFAULT_DAMAGE);
        ammo.speed = Weapon.attempt(props, "speed", DEFAULT_SPEED);
        return ammo;
    }

    public Ammo copy(Mass src) {
        Ammo ammo = new Ammo(getHull(), name, info);
        ammo.shortlived = true;
        ammo.suffersdrag = false;
        ammo.ttl = ttl;
        ammo.damage = damage;
        ammo.speed = speed;
        ammo.hull = hull.copy();
        ammo.setPosition(src);
        ammo.x[1] = src.x[1] + Math.cos(src.getA(0)) * speed;
        ammo.y[1] = src.y[1] + Math.sin(src.getA(0)) * speed;
        ammo.setFaction(src.getFaction());
        ammo.source = src;
        return ammo;
    }

    public void step(double t) {
        super.step(t);

        boolean teamDamage = Config.teamDamage();
        Faction team = getFaction();
        for (Mass m : Universe.get().getObjects()) {
            if (!m.shortlived && m.isActive()) {
                if (m != source && (teamDamage || m.getFaction() != team)) {
                    for (double dt = t; dt > 0; dt -= t / HIT_DIVISION) {
                        double tx = x[0] - x[1] * dt;
                        double ty = y[0] - y[1] * dt;
                        if (m.getHit().contains(tx, ty)) {
                            hit(m);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void hit(Mass m) {
        /* TODO: calculate damage
                 remove object from the universe if below 0. */
        m.damage(damage);
        if (Config.showDamage()) {
            Mass txt = new Mass(new Hull(new Model("" + damage)));
            txt.setPosition(this);
            txt.setFaction(getFaction());
            txt.shortlived = true;
            txt.ttl = 15;
            txt.setA(0, 0);
            txt.setY(-0.7, 1);
            txt.setSize(2.0);
            Universe.get().add(txt);
        }
        zenThing();
    }
}
