package hypernova.gui.backgrounds;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import hypernova.audio.MinimWrapper;
import hypernova.gui.Background;
import hypernova.gui.Viewer;

public class MusicStarfield extends Background{
    public static final int STAR_SEED = 0x9d2c5680;
    private static boolean clearScreen = true;
    private static double lastX = 0;
    public static BackgroundType bg = BackgroundType.MOVE;
    public enum BackgroundType { ASTERIK 
                               , MOVE
                               , ROTATE 
                               };
    
    public static void setClearScreen(boolean doClear) { clearScreen = doClear; }

    public void drawBackground(Graphics g, Graphics2D g2d, double focusX, double focusY) {
        float x = (MinimWrapper.fft(4))[0];
        if(bg == BackgroundType.ASTERIK)
        {
          if(x < 25) g.setColor(Color.BLACK);
          else if(x > 75) g.setColor(Color.WHITE);
          else if (x < 50) g.setColor(Color.DARK_GRAY);
          else g.setColor(Color.LIGHT_GRAY);
        } else { 
          g.setColor(Color.BLACK);
        }
 
        boolean tmpClear = true;
        if(bg == BackgroundType.ROTATE && x >= lastX) tmpClear = false;
        lastX = x;
        if(clearScreen && tmpClear) g.fillRect(0, 0, width, height);
        g2d.translate(width / 2, height / 2); 
        for (int i = quality + 1; i > 0; i--) {
            drawStars(g2d, (int) focusX / i, (int) focusY / i, i);
        }
    }

    public static final int STAR_TILE_SIZE = 256;
    public static float r[] = {0,0,0};
    public void drawStars(Graphics2D g, int xoff, int yoff, int starscale) {
        int size = STAR_TILE_SIZE / starscale;
        int w = (int) (width / (scale / Viewer.DEFAULT_SCALE));
        int h = (int) (height / (scale / Viewer.DEFAULT_SCALE));
        float c = (MinimWrapper.fft(4))[0];
        int c1 = (int)(MinimWrapper.fft(4))[1];
        int c2 = (int)(MinimWrapper.fft(4))[2];
        int c3 = (int)(MinimWrapper.fft(4))[3];

        /* Set colors */
        if( bg == BackgroundType.ASTERIK )
        {
          if(starscale == 1) g.setColor(Color.cyan);
          else if(starscale == 2) g.setColor(Color.blue);
          else if(starscale == 3) g.setColor(Color.yellow);
        } else { 
          if(starscale == 1) g.setColor(new Color((30 + c1*100) % 255, (30 + c2*100) % 255, (30 + c3*100) % 255));
          else if(starscale == 2) g.setColor(new Color( (30 + c2*100) % 255, (30 + c3*100) % 255, (30 + c1*100) % 255));
          else if(starscale == 3) g.setColor(new Color((30 + c3*100) % 255, (30 + c1*100) % 255, (30 + c2*100) % 255));
        }
        /* Top-left tile's top-left position. */
        int sx = ((xoff - w/2) / size) * size - size;
        int sy = ((yoff - h/2) / size) * size - size;
        
        if( bg == BackgroundType.ROTATE)
        {
          float deltaVal = (MinimWrapper.fft(4))[starscale] / 250;
          if(starscale == 2) deltaVal = -deltaVal;
          r[starscale - 1] += deltaVal;
          g.rotate(r[starscale-1]);//(MinimWrapper.fft(4))[starscale] / 50);
        }
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
                    switch(bg)
                    {
                        case MOVE:
                            g.drawOval(px - c1, py - c2, 2 + c3,  2 + c3); 
                            break;
                        case ASTERIK:
                            int d = (int)(MinimWrapper.fft(4))[starscale];
                            g.drawLine(px - d, py - d, px+d, py+ d);
                            g.drawLine(px + d, py - d, px-d, py+d);
                            g.drawLine(px-d, py , px + d, py);
                            g.drawLine(px, py-d , px, py+d);
                            break;
                        case ROTATE:
                            g.draw3DRect(px, py,  5, 5, true); //5 + (int)(c/10), 5 + (int)(c/10), true);
                            break;
                    }
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
