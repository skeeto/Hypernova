package hypernova.gui.backgrounds;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import hypernova.MinimWrapper;
import hypernova.gui.Background;
import hypernova.gui.Viewer;

public class MusicStarfield extends Background{
    public static final float[] STAR_COLORS = {1.0f, 0.66f, 0.33f};
    public static final int STAR_SEED = 0x9d2c5680;
    private static boolean clearScreen = true;
    
    public static void setClearScreen(boolean doClear) { clearScreen = doClear; }

    public void drawBackground(Graphics g, Graphics2D g2d, double focusX, double focusY) {
        float x = (MinimWrapper.fft(4))[0];
        if(x < 25) g.setColor(Color.BLACK);
        else if(x > 75) g.setColor(Color.WHITE);
        else if (x < 50) g.setColor(Color.DARK_GRAY);
        else g.setColor(Color.LIGHT_GRAY);
        if(clearScreen) g.fillRect(0, 0, width, height);
        g2d.translate(width / 2, height / 2);
        for (int i = quality + 1; i > 0; i--) {
            float c = (float) (STAR_COLORS[i - 1] * scale / Viewer.DEFAULT_SCALE);
            c = Math.min(c, STAR_COLORS[i - 1]);
            g.setColor(new Color(c, c, c));
            drawStars(g2d, (int) focusX / i, (int) focusY / i, i);
        }
    }

    public static final int STAR_TILE_SIZE = 256;
    public void drawStars(Graphics2D g, int xoff, int yoff, int starscale) {
        int size = STAR_TILE_SIZE / starscale;
        int w = (int) (width / (scale / Viewer.DEFAULT_SCALE));
        int h = (int) (height / (scale / Viewer.DEFAULT_SCALE));
        int c = (int)(MinimWrapper.fft(4))[starscale];

        /* Set colors */
        if(starscale == 1) g.setColor(Color.cyan);
        else if(starscale == 2) g.setColor(Color.blue);
        else if(starscale == 3) g.setColor(Color.yellow);
        
        /* Top-left tile's top-left position. */
        int sx = ((xoff - w/2) / size) * size - size;
        int sy = ((yoff - h/2) / size) * size - size;

        /* Draw each tile currently in view. */
        for (int i = sx; i <= w + sx + size * 3; i += size) {
            for (int j = sy; j <= h + sy + size * 3; j += size) {
                int hash = mix(STAR_SEED, i, j);
                for (int n = 0; n < 3; n++) {
                    int px = (hash % size) + (i - xoff);
                    hash >>= 3;
                    int py = (hash % size) + (j - yoff);
                    hash >>= 3;
                    px *= scale / Viewer.DEFAULT_SCALE;
                    py *= scale / Viewer.DEFAULT_SCALE;
                    g.drawLine(px - c, py - c, px+c, py+ c);
                    g.drawLine(px + c, py - c, px-c, py+c);
                    g.drawLine(px-c, py , px + c, py);
                    g.drawLine(px, py-c , px, py+c);

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
