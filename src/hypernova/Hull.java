package hypernova;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import java.awt.geom.Point2D;

import org.apache.log4j.Logger;

import hypernova.gui.Model;

public class Hull {
    public static final double DEFAULT_HP = 5.0;
    public static final double DEFAULT_MASS = 5.0;
    public static final String DEFAULT_MODEL = "simple";

    private static Map<String, Hull> cache = new HashMap<String, Hull>();

    public final String name, info;

    private double hp;
    private double mass;
    private Model model;
    private int numweapons;
    private Point2D.Double[] weaponslots;

    private static Logger log = Logger.getLogger("Hull");

    protected Hull(String name, String info) {
        this.name = name;
        this.info = info;
    }

    /** Create an anonymous Hull from the given model. */
    public Hull(Model model) {
        name = null;
        info = null;
        this.model = model;
        hp = DEFAULT_HP;
        mass = DEFAULT_MASS;
    }

    public static Hull get(String name) {
        Hull hull = cache.get(name);
        if (hull != null) return hull.copy();
        String filename = "parts/" + name + ".hull";
        log.debug("Loading hull '" + name + "' (" + filename + ")");
        Properties props = new Properties();
        try {
            props.load(Weapon.class.getResourceAsStream(filename));
        } catch (java.io.IOException e) {
            /* TODO handle this more gracefully. */
            log.error("Failed to load hull '" + name + "': " + e.getMessage());
            return null;
        }

        hull = new Hull(props.getProperty("name"), props.getProperty("info"));
        hull.hp = Weapon.attempt(props, "hp", DEFAULT_HP);
        hull.mass = Weapon.attempt(props, "mass", DEFAULT_MASS);
        String model = props.getProperty("model");
        if (model == null)
            model = DEFAULT_MODEL;
        hull.model = Model.get(model);
        hull.numweapons = (int) Weapon.attempt(props, "numweapons", 0);
        hull.weaponslots = slots(props.getProperty("weaponslots"),
                                 hull.numweapons);
        cache.put(name, hull);
        return hull.copy();
    }

    private static Point2D.Double[] slots(String str, int n) {
        if (str == null || n == 0)
            return new Point2D.Double[0];
        double[] list = Model.readList(str, 0);
        Point2D.Double[] slots = new Point2D.Double[n];
        for (int i = 0; i < n; i++) {
            slots[i] = new Point2D.Double(list[i * 2], list[i * 2 + 1]);
        }
        return slots;
    }

    public Model getModel() {
        return model;
    }

    public int numWeapons() {
        return numweapons;
    }

    public double getMass() {
        return mass;
    }

    public double getHP() {
        return hp;
    }

    public void setHP(double val) {
        hp = val;
    }

    public Hull copy() {
        Hull copy = new Hull(name, info);
        copy.hp = hp;
        copy.mass = mass;
        copy.model = model.copy();
        copy.numweapons = numweapons;
        if (numweapons > 0)
            copy.weaponslots = new Point2D.Double[numweapons];
        return copy;
    }
}
