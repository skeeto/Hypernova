package hypernova.gui;

import java.util.List;
import java.util.Observer;
import java.util.Observable;

import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import hypernova.Ship;
import hypernova.Mass;
import hypernova.Universe;
import hypernova.Hypernova;
import hypernova.KeyboardPilot;

public class Viewer extends JComponent implements Observer {
    public static final long serialVersionUID = 850159523722721935l;

    public static final double ZOOM_RATE = 1.2;

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
    private double scale = 2.0;
    private double targetScale = scale;
    private int quality = 2; /* 0 - 2 quality setting. */

    private static Logger log = Logger.getLogger("gui.Viewer");

    public Viewer(Universe state) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setOpaque(true);
        universe = state;
        universe.addObserver(this);
        addKeyListener(new KeyboardPilot());
        addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    log.trace("keyPressed() " + e);
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_PAGE_UP:
                        setScale(getScale() * ZOOM_RATE);
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        setScale(getScale() * (1 / ZOOM_RATE));
                        break;
                    default:
                        log.trace("Unkown key " + e.getKeyCode());
                    }
                }
                @Override
                public void keyReleased(KeyEvent e) {}
                @Override
                public void keyTyped(KeyEvent e) {}
            });
    }

    public void setQuality(int q) {
        log.info("quality adjusted to " + q);
        quality = Math.max(q, 0);
    }

    public int getQuality() {
        return quality;
    }

    public void setScale(double scale) {
        targetScale = scale;
    }

    public double getScale() {
        return targetScale;
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

        scale = (0.8 * scale + 0.2 * targetScale);

        Mass player = universe.getPlayer();
        double px = player.getX(0);
        double py = player.getY(0);

        for (int i = Math.min(quality + 1, stars.length); i > 0; i--) {
            g.setColor(stars[i - 1]);
            drawStars(g2d, (int) px / i, (int) py / i, i);
        }

        /* Set up graphics */
        if (quality > 0) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                                 RenderingHints.VALUE_STROKE_PURE);
        }
        if (quality > 1) {
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                                 RenderingHints.VALUE_RENDER_QUALITY);
        }

        g2d.translate(-(px * scale - getWidth() / 2),
                      -(py * scale - getHeight() / 2));
        g2d.scale(scale, scale);
        List<Mass> objects = universe.getObjects();
        synchronized (objects) {
            for (Mass m : objects) {
                drawMass(g2d, m);
            }
        }
    }

    public void drawMass(Graphics2D g, Mass m) {
        g.setColor(m.getFaction().getColor());
        Model model = m.getModel();
        Shape[] shapes = model.getShapes();
        boolean[] filled = model.getFilled();
        for (int i = 1; i < shapes.length; i++) {
            if (filled[i])
                g.fill(shapes[i]);
            else
                g.draw(shapes[i]);
        }
    }

    public void drawStars(Graphics2D g, int xoff, int yoff, int scale) {
        int size = 256 / scale;
        int sx = (xoff / size) * size - size;
        int sy = (yoff / size) * size - size;
        for (int i = sx; i <= getWidth() + sx + size * 3; i += size) {
            for (int j = sy; j <= getHeight() + sy + size * 3; j += size) {
                int hash = mix(STAR_SEED, i, j);
                for (int n = 0; n < 3; n++) {
                    int px = (hash % size) + (i - xoff);
                    hash >>= 3;
                    int py = (hash % size) + (j - yoff);
                    hash >>= 3;
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
