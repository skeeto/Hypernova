package hypernova;

import com.google.common.primitives.Doubles;
import java.util.Properties;
import java.io.IOException;

import clojure.lang.RT;
import clojure.lang.Var;

import org.apache.log4j.Logger;

public abstract class Activity {
    protected static Logger log = Logger.getLogger("Activity");

    private String script;

    public static Activity get(String name) {
        Properties prop = new Properties();
        String fname = "activities/" + name + ".act";
        log.debug("Loading activity " + name + " (" + fname + ")");
        try {
            prop.load(Activity.class.getResourceAsStream(fname));
        } catch (IOException ex) {
            log.error("Failed to load activity " + name);
            return null;
        }
        return new Activity(prop.getProperty("script")) {
            public void realize(double x, double y) {
                ActivityRuntime.get().execute(this, x, y);
            }
        };
    }

    protected Activity() {
    }

    public String getScript() {
        return script;
    }

    private Activity(String script) {
        this.script = script;
    }

    public abstract void realize(double x, double y);
}
