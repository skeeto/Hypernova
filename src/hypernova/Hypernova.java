package hypernova;

import javax.swing.JFrame;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import hypernova.gui.Viewer;

public class Hypernova {
    public static final String TITLE = "Hypernova";

    public static Universe universe;
    private static Viewer viewer;

    public static boolean debug = false;
    private static Logger log = Logger.getRootLogger();

    public static void main(String[] args) {
        /* Fix for poor OpenJDK performance. */
        System.setProperty("sun.java2d.pmoffscreen", "false");

        /* Prepare logging. */
        BasicConfigurator.configure();
        if (debug)
            log.setLevel(Level.TRACE);

        Sound.init();

        universe = new Universe();
        viewer = new Viewer(universe);

        JFrame frame = new JFrame(TITLE);
        frame.add(viewer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        viewer.requestFocusInWindow();

        universe.start();
    }
}
