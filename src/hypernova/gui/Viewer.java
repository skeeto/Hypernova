package hypernova.gui;

import java.util.List;
import java.util.Observer;
import java.util.Observable;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;

import hypernova.Ship;
import hypernova.Mass;
import hypernova.Universe;

public class Viewer extends JComponent implements Observer {
    public static final long serialVersionUID = 850159523722721935l;

    public static final double PLAYER_TURN = 0.2;

    /* Starting size. */
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private final Universe universe;
    private double scale = 1.0;
    private int quality = 2; /* 0 - 2 quality setting. */

    public Viewer(Universe state) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setOpaque(true);
        universe = state;
        universe.addObserver(this);

        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                Ship player = universe.getPlayer();
                switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    player.addA(-PLAYER_TURN, 1);
                    break;
                case KeyEvent.VK_RIGHT:
                    player.addA(PLAYER_TURN, 1);
                    break;
                case KeyEvent.VK_UP:
                    player.setEngines(true);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setFire(0, true);
                    break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Ship player = universe.getPlayer();
                switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    player.addA(PLAYER_TURN, 1);
                    break;
                case KeyEvent.VK_RIGHT:
                    player.addA(-PLAYER_TURN, 1);
                    break;
                case KeyEvent.VK_UP:
                    player.setEngines(false);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setFire(0, false);
                    break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}
        });
    }

    public void setQuality(int q) {
        quality = Math.max(q, 0);
    }

    public int getQuality() {
        return quality;
    }

    @Override
    public void update(Observable o, Object msg) {
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.translate(getWidth() / 2, getHeight() / 2);

        Graphics2D g2d = (Graphics2D) g;
        if (quality > 0) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
        }

        Mass player = universe.getPlayer();
        double xoff = player.getX(0);
        double yoff = player.getY(0);
        List<Mass> objects = universe.getObjects();
        synchronized (objects) {
            for (Mass m : objects) {
                drawMass(g2d, m, xoff, yoff);
            }
        }
    }

    public void drawMass(Graphics2D g, Mass m, double xoff, double yoff) {
        g.setColor(Color.GREEN);
        int size = 25;
        int reach = 25;

        /* Ship details */
        double x = m.getX(0);
        double y = m.getY(0);
        double a = m.getA(0);

        /* Center pixel. */
        int cx = (int) ((x - xoff) * scale);
        int cy = (int) ((y - yoff) * scale);

        g.drawOval(cx - size / 2, cy - size / 2, size, size);
        g.drawLine(cx, cy,
                   (int) (Math.cos(a) * scale * reach) + cx,
                   (int) (Math.sin(a) * scale * reach) + cy);
    }
}
