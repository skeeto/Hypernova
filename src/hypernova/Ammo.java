package hypernova;

import java.util.Properties;
import java.awt.geom.Point2D;

import org.apache.log4j.Logger;

import hypernova.gui.Model;

public class Ammo extends Mass {
    public static final int DEFAULT_TTL = 200;
    public static final double DEFAULT_SPEED = 1.5;
    public static final double DEFAULT_DAMAGE = 1.0;
    public static final String DEFAULT_MODEL = "bolt";
    public static final double HIT_DIVISION = 8.0;

    public final String name, info, type;

    private double damage;
    private double speed;
    private Mass source;

    private static Logger log = Logger.getLogger("Ammo");

    private Ammo(Hull hull, String name, String info, String type) {
        super(hull);
        this.name = name;
        this.info = info;
        this.type = type;
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
                                   props.getProperty("info"),
                                   props.getProperty("type"));
        ammo.ttl = (int) Weapon.attempt(props, "ttl", DEFAULT_TTL);
        ammo.damage = Weapon.attempt(props, "damage", DEFAULT_DAMAGE);
        ammo.speed = Weapon.attempt(props, "speed", DEFAULT_SPEED);
        return ammo;
    }

    public Ammo copy(Mass src, Point2D.Double p) {
        Ammo ammo = new Ammo(getHull(), name, info, type);
        ammo.shortlived = true;
        ammo.suffersdrag = false;
        ammo.ttl = ttl;
        ammo.damage = damage;
        ammo.speed = speed;
        ammo.hull = hull.copy();
        ammo.setPosition(src);

        // Intentionally backwards :P
        double x = p.getY();
        double y = p.getX();
        double a = src.getA(0);
        double phi = Math.atan(y / x);
        double r   = Math.sqrt(x*x + y*y);
        ammo.x[0] = src.x[0] + r * Math.cos(a - phi);
        ammo.y[0] = src.y[0] + r * Math.sin(a - phi);
        ammo.x[1] = src.x[1] + speed * Math.cos(a);
        ammo.y[1] = src.y[1] + speed * Math.sin(a);
        ammo.setFaction(src.getFaction());
        ammo.source = src;
        return ammo;
    }

    public void collision(Mass m) {
        boolean teamDamage = Config.teamDamage();
        Faction team = getFaction();
             
        if (m != source && (teamDamage || m.getFaction() != team)) {
          if ("stop".equals(type) ) {
            m.setX(0, 1);
            m.setY(0, 1); 
          } else {
            Collision.hit(m,this,damage,true);
          }
        }
    }
}
