package hypernova;

import java.net.URL;

import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.applet.AudioClip;
import java.applet.Applet;

import org.apache.log4j.Logger;

public class Sound {
    public static boolean enabled;

    private static Executor pool = Executors.newCachedThreadPool();
    private static Map<String, ClipPool> clips
        = Collections.synchronizedMap(new HashMap<String, ClipPool>());
    private static Logger log = Logger.getLogger("Sound");

    public static void init() {
        log.info("Setting up sound");
        enabled = true;
    }

    public static void play(String name) {
        if (!enabled) return;
        ClipPool clip = clips.get(name);
        if (clip == null) {
            /* A race condition may create multiple pools, but that's ok. */
            log.debug("Creating new ClipPool: " + name);
            URL url = Sound.class.getResource("sounds/" + name + ".wav");
            clip = new ClipPool(url);
            clips.put(name, clip);
        }
        clip.play();
    }

    private static class ClipPool {
        private final URL url;
        private final Queue<AudioClip> cache
            = new ConcurrentLinkedQueue<AudioClip>();

        public ClipPool(URL url) {
            this.url = url;
        }

        public void play() {
            AudioClip qclip = cache.poll();
            final AudioClip clip
                = qclip == null ? Applet.newAudioClip(url) : qclip;
            pool.execute(new Runnable() {
                    public void run() {
                        try {
                            clip.play();
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            log.debug("Audio exception: " + e);
                        }
                        cache.add(clip);
                    }
                });
        }
    }
}
