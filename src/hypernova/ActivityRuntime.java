package hypernova;

import java.util.Map.Entry;
import com.google.common.primitives.Doubles;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;

import clojure.lang.RT;
import clojure.lang.Var;
import clojure.lang.Binding;
import clojure.lang.Compiler;
import clojure.lang.Symbol;
import clojure.lang.PersistentHashMap;
import clojure.lang.Associative;
import clojure.lang.Namespace;

import org.apache.log4j.Logger;

public class ActivityRuntime {
    private static Logger log = Logger.getLogger("ActivityRuntime");

    private Symbol CLOJURE_MAIN;
    private Var REQUIRE;

    private Symbol API;
    private Var API_LOADER;

    private Var API_REPL;

    private static ActivityRuntime INSTANCE = null;

    private ActivityRuntime() throws Exception {
        CLOJURE_MAIN = Symbol.intern("clojure.main");
        REQUIRE = RT.var("clojure.core", "require");
        API = Symbol.intern("hypernova.activities.api");

        REQUIRE.invoke(CLOJURE_MAIN);
        REQUIRE.invoke(API);

        API_LOADER = RT.var("hypernova.activities.api", "loader");
        API_REPL = RT.var("hypernova.activities.api", "repl");
    }

    public static ActivityRuntime get() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new ActivityRuntime();
            } catch (Exception ex) {
                System.out.println("baddness! " + ex);
                System.exit(1);
            }
        }

        return INSTANCE;
    }

    public void execute(Activity activity, Double x, Double y) {
        try {
            Object result = API_LOADER.applyTo(RT.seq(RT.vector(activity.getScript(), x, y)));
            System.out.println("result: " + result);
        } catch (Exception ex) {
            System.out.println("something went poorly: " + ex);
            System.exit(1);
        }
    }

    public void startRepl() {
        log.info("Starting a REPL");
        new Thread() {
            @Override
            public void run() {
                try {
                    API_REPL.applyTo(RT.seq(RT.vector()));
                } catch (Exception e) {
                    log.warn("REPL threw exception " + e);
                }
            }
        } .start();
    }
}
