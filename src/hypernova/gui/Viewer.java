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

import com.google.common.base.Objects;

import hypernova.Ship;
import hypernova.Mass;
import hypernova.Universe;

public class Viewer extends JComponent implements Observer {
    public static final long serialVersionUID = 850159523722721935l;

    public static final Color[] stars = {
        new Color(0xFF, 0xFF, 0xFF),
        new Color(0xAF, 0xAF, 0xAF),
        new Color(0x4F, 0x4F, 0x4F),
    };
    public static final int STAR_SEED = 0x9d2c5680;

    /* Starting size. */
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private final Universe universe;
    private double scale = 10.0;
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
                    player.turnLeft(true);
                    break;
                case KeyEvent.VK_RIGHT:
                    player.turnRight(true);
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
                    player.turnLeft(false);
                    break;
                case KeyEvent.VK_RIGHT:
                    player.turnRight(false);
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
        Graphics2D g2d = (Graphics2D) g;

        Mass player = universe.getPlayer();
        double xoff = player.getX(0);
        double yoff = player.getY(0);

        for (int i = Math.min(quality + 1, stars.length); i > 0; i--) {
            g.setColor(stars[i - 1]);
            drawStars(g2d, (int) xoff / i, (int) yoff / i, i);
        }

        /* Set up graphics */
        if (quality > 0) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g.translate(getWidth() / 2, getHeight() / 2);
        List<Mass> objects = universe.getObjects();
        synchronized (objects) {
            for (Mass m : objects) {
                drawMass(g2d, m, xoff, yoff);
            }
        }
    }

    public void drawMass(Graphics2D g, Mass m, double xoff, double yoff) {
        /* Ship details */
        double x = m.getX(0);
        double y = m.getY(0);
        double a = m.getA(0);
        int cx = (int) ((x - xoff) * scale);
        int cy = (int) ((y - yoff) * scale);

        g.setColor(Color.GREEN);
        Model model = m.getModel();
        for (Polygon p : model.getPolygons()) {
            p.rotate(m.getA(0));
            for (int i = 0; i < p.xs.length - 1; i++) {
                g.drawLine((int) (p.rxs[i] * scale) + cx,
                           (int) (p.rys[i] * scale) + cy,
                           (int) (p.rxs[i+1] * scale) + cx,
                           (int) (p.rys[i+1] * scale) + cy);
            }
        }

        // g.drawOval(cx - size / 2, cy - size / 2, size, size);
        // g.drawLine(cx, cy,
        //            (int) (Math.cos(a) * scale * reach) + cx,
        //            (int) (Math.sin(a) * scale * reach) + cy);
    }

    public void drawStars(Graphics2D g, int xoff, int yoff, int scale) {
        int size = 128 / scale;
        int sx = (xoff / size) * size - size;
        int sy = (yoff / size) * size - size;
        for (int i = sx; i <= getWidth() + sx + size * 3; i += size) {
            for (int j = sy; j <= getHeight() + sy + size * 3; j += size) {
                int hash = mix(STAR_SEED, i, j);
                if ((hash & 1) == 1) {
                    int px = (hash % size) + (i - xoff);
                    int py = (hash % size) + (j - yoff);
                    g.drawLine(px, py, px, py);
                }
            }
        }
    }

    /** Robert Jenkins' 96 bit Mix Function.
     * @param a random bits
     * @param b random bits
     * @param c the "key" to be hashed
     */
    private static int mix(int a, int b, int c) {
        a=a-b;  a=a-c;  a=a^(c >>> 13);
        b=b-c;  b=b-a;  b=b^(a << 8);
        c=c-a;  c=c-b;  c=c^(b >>> 13);
        a=a-b;  a=a-c;  a=a^(c >>> 12);
        b=b-c;  b=b-a;  b=b^(a << 16);
        c=c-a;  c=c-b;  c=c^(b >>> 5);
        a=a-b;  a=a-c;  a=a^(c >>> 3);
        b=b-c;  b=b-a;  b=b^(a << 10);
        c=c-a;  c=c-b;  c=c^(b >>> 15);
        return c;
    }
}
