package hypernova;

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

public class Hypernova {
    public static final String PROGRAM = "Hypernova";
    public static final String VERSION = "0.1";

    public static boolean debug = false;

    private static Viewer viewer;
    private static Logger log = Logger.getRootLogger();

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

        viewer = new Viewer();
        Universe.get().initialize();

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

        /* Initiate GUI */
        JFrame frame = new JFrame(PROGRAM);
        frame.add(viewer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        viewer.requestFocusInWindow();

        if (!line.hasOption("nosound")) {
            Sound.init();
        }

        if (line.hasOption("repl")) {
            ActivityRuntime.get().startRepl();
        }

        Universe.start();
        Universe.get().addActivity("test", 0, 0);

        Activity battle = new hypernova.activities.FactoryBattle();
        Universe.get().addActivity(battle, -500, -500);
    }

    private static CommandLine parseArgs(String[] args) {
        Options opts = new Options();
        String[][] str = {
            {"h", "help",    "f", "print this message"},
            {"v", "version", "f", "print program version"},
            {"d", "debug",   "f", "turn on extra debugging info"},
            {"q", "quality", "t", "set display quality (0-2)"},
            {"s", "nosound", "n", "disable sound"},
            {"r", "repl",    "n", "expose a REPL on standard IO"},
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
        return line;
    }

    private static void printVersion() {
        System.out.println(PROGRAM + " " + VERSION);
    }
}
