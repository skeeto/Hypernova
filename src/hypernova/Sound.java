package hypernova;

import org.apache.log4j.Logger;

public class Sound {
    public static boolean enabled;

    private static Logger log = Logger.getLogger("Sound");

    public static void init() {
        log.info("Setting up sound");
        enabled = true;
    }

    public static void play(String name) {
    }
}
