package hypernova;

import java.util.Properties;

import java.awt.geom.Point2D;

import org.apache.log4j.Logger;

import hypernova.gui.Model;

public class Hull {
    public static final double DEFAULT_HP = 5.0;
    public static final double DEFAULT_MASS = 5.0;
    public static final String DEFAULT_MODEL = "simple";

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

    public static Hull getHull(String name) {
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

        Hull hull = new Hull(props.getProperty("name"),
                             props.getProperty("info"));
        hull.hp = Weapon.attempt(props, "hp", DEFAULT_HP);
        hull.mass = Weapon.attempt(props, "mass", DEFAULT_MASS);
        String model = props.getProperty("model");
        if (model == null)
            model = DEFAULT_MODEL;
        hull.model = Model.getModel(model);
        hull.numweapons = (int) Weapon.attempt(props, "numweapons", 0);
        hull.weaponslots = slots(props.getProperty("weaponslots"),
                                 hull.numweapons);
        return hull;
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
}
