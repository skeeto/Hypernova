package hypernova;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Engine {
    private static Map<String, Engine> cache = new HashMap<String, Engine>();
    private static Logger log = Logger.getLogger("Engine");

    private final String name, info;
    private double thrust, maneuverability;
    private double mass;
    private int numengines;

    protected Engine(String name, String info) {
        this.name = name;
        this.info = info;
    }

    public static Engine get(String name) {
        Engine engine = cache.get(name);
        if (engine != null) return engine;
        String filename = "parts/" + name + ".engine";
        log.debug("Loading engine '" + name + "' (" + filename + ")");
        Properties props = new Properties();
        try {
            props.load(Engine.class.getResourceAsStream(filename));
        } catch (java.io.IOException e) {
            /* TODO handle this more gracefully. */
            log.error("Failed to load engine '" + name + "': "
                      + e.getMessage());
            return null;
        }

        engine = new Engine(props.getProperty("name"),
                            props.getProperty("info"));
        engine.maneuverability = Weapon.attempt(props, "maneuverability", 0);
        engine.thrust = Weapon.attempt(props, "thrust", 0);
        engine.mass = Weapon.attempt(props, "mass", 0);
        engine.numengines = (int) Weapon.attempt(props, "numengines", 0);
        cache.put(name, engine);
        return engine;
    }

    public double getThrust() {
        return thrust;
    }

    public double getManeuverability() {
        return maneuverability;
    }

    public double getMass() {
        return mass;
    }
}
