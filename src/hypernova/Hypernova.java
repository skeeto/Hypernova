package hypernova;


import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.DisplayMode;
import java.awt.Toolkit;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Component;
import java.awt.MediaTracker;

import javax.swing.JFrame;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;

import hypernova.gui.Viewer;
import hypernova.gui.Transition;
import hypernova.audio.SongPlaylist;
import hypernova.audio.MinimWrapper;

public class Hypernova {
    public static final String PROGRAM = "Hypernova";
    public static final String VERSION = "0.1";

    public static boolean debug = false;

    private static Logger log = Logger.getRootLogger();
    private static Viewer viewer;

    public static void main(String[] args) {
        /* Fix for poor OpenJDK performance. */
        System.setProperty("sun.java2d.pmoffscreen", "false");

        /* Prepare logging. */
        BasicConfigurator.configure();
        log.setLevel(Level.WARN);

        /* Parse the command line options. */
        CommandLine line = parseArgs(args);
        if (line.hasOption("debug")) {
            debug = true;
            log.setLevel(Level.DEBUG);
            log.info("extra debugging information enabled");
        }

        if (line.hasOption("accel")) {
            System.setProperty("sun.java2d.opengl","true");
            System.setProperty("sun.java2d.translaccel","true");
            System.setProperty("sun.java2d.ddforcevram","true");
        }

        /* Fullscreen Setup */
        JFrame frame = new JFrame(PROGRAM);
        if (line.hasOption("fullscreen"))
        {
            frame.setUndecorated(true);
            String str = line.getOptionValue("fullscreen");
            try {
                int modeNum = Integer.parseInt(str);
                GraphicsEnvironment env = GraphicsEnvironment.
                                          getLocalGraphicsEnvironment();
                GraphicsDevice device = env.getDefaultScreenDevice();
                DisplayMode[] modes = device.getDisplayModes();
                DisplayMode newDisplayMode = modes[modeNum];
                DisplayMode oldDisplayMode = device.getDisplayMode();
                device.setFullScreenWindow(frame);
                device.setDisplayMode(newDisplayMode);
                Viewer.WIDTH  = newDisplayMode.getWidth();
                Viewer.HEIGHT = newDisplayMode.getHeight();
            } catch (Exception e) {
                System.err.println("Invalid mode '" + str + "'");
                e.printStackTrace();
                System.exit(1);
            }
        }


        /* Determine quality settings. */
        if (line.hasOption("quality")) {
            String str = line.getOptionValue("quality");
            try {
                int level = Integer.parseInt(str);
                if (level < 0 || level > 2) throw new Exception();
                viewer.setQuality(level);
            } catch (Throwable t) {
                System.err.println("Unknown quality level '" + str + "'");
                System.exit(1);
            }
        }

        viewer = new Viewer();
        Universe.get().initialize();
        Transition.setViewer(viewer);

        /* Hide mouse cursor */
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image transCursorImage = toolkit.getImage("./images/1px.png");
        MediaTracker mediaTracker = new MediaTracker(frame);
        mediaTracker.addImage(transCursorImage, 0);
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            System.exit(1);
        }
        Cursor transCursor = toolkit.createCustomCursor(transCursorImage,
                             new Point(0,0), "blank");
        ((Component)frame).setCursor(transCursor);

        /* Initiate GUI */
        frame.add(viewer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        viewer.requestFocusInWindow();

        if (line.hasOption("repl")) {
            ActivityRuntime.get().startRepl();
        }

        /* Sound */
        MinimWrapper.init();
        SongPlaylist.addFile("mp3s/foo.m3u");
        SongPlaylist.setShuffle(true);
        SongPlaylist.debug();
        SongPlaylist.forwardSong();
        
        Universe.start();
        if (line.hasOption("load")) {
            String str = line.getOptionValue("load");
            try {
                int slot = Integer.parseInt(str);
                if (slot < 0 || slot > 10) throw new Exception();
                SaveGame.load(slot);
            } catch (Throwable t) {
                System.err.println("Unknown slot to load from '" + str + "'");
                System.exit(1);
            }
        } else {
            Universe.get().loadUniverse(new StartScreen());
        }
    }

    private static CommandLine parseArgs(String[] args) {
        Options opts = new Options();
        String[][] str = {
            {"h", "help",       "f", "print this message"},
            {"v", "version",    "f", "print program version"},
            {"a", "accel",      "f", "use hardware acceleration"},
            {"m", "modes",      "f", "print available graphics modes"},
            {"d", "debug",      "f", "turn on extra debugging info"},
            {"q", "quality",    "t", "set display quality (0-2)"},
            {"f", "fullscreen", "t", "fullscreen display (must specify mode"},
            {"l", "load",       "t", "load a game (0-10)"},
            {"s", "nosound",    "n", "disable sound"},
            {"r", "repl",       "n", "expose a REPL on standard IO"},
        };
        for (String[] o : str) {
            opts.addOption(new Option(o[0], o[1], "t".equals(o[2]), o[3]));
        }
        CommandLineParser parser = new GnuParser();
        CommandLine line = null;
        try {
            line = parser.parse(opts, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (line.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Hypernova [options]", opts);
            System.exit(0);
        }
        if (line.hasOption("version")) {
            printVersion();
            System.exit(0);
        }
        if (line.hasOption("modes")) {
            GraphicsEnvironment env = GraphicsEnvironment.
                                      getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            DisplayMode[] modes = device.getDisplayModes();
            for (int i = 0; i < modes.length; i++) {
                System.out.println("Mode " + i);
                System.out.println("  Width:   " + modes[i].getWidth());
                System.out.println("  Height:  " + modes[i].getHeight());
                System.out.println("  Depth:   " + modes[i].getBitDepth());
                System.out.println("  Refresh: " + modes[i].getRefreshRate());
            }
            System.exit(0);
        }
        return line;
    }

    private static void printVersion() {
        System.out.println(PROGRAM + " " + VERSION);
    }
  
    public static Viewer getViewer() {
      return viewer;
    }
}
