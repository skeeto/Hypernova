package hypernova;

import javax.swing.JFrame;

import hypernova.gui.Viewer;

public class Hypernova {
    public static final String TITLE = "Hypernova";

    private static Universe universe = new Universe();
    private static Viewer viewer = new Viewer(universe);

    public static void main(String[] args) {
        /* Fix for poor OpenJDK performance. */
        System.setProperty("sun.java2d.pmoffscreen", "false");

        JFrame frame = new JFrame(TITLE);
        frame.add(viewer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        viewer.requestFocusInWindow();

        universe.start();
    }
}
