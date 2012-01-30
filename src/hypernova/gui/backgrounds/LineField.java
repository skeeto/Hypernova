package hypernova.gui.backgrounds;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import hypernova.audio.MinimWrapper;
import hypernova.gui.Background;
import hypernova.gui.Viewer;

public class LineField extends Background{
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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g2d.translate(width / 2, height / 2); 
        drawLines(g2d, (int)focusX, (int)focusY);
    }

    public void drawLines(Graphics2D g, int x, int y) {
        float c0 = (MinimWrapper.fft(4))[0];
        int c1 = (int)(MinimWrapper.fft(4))[1];
        int c2 = (int)(MinimWrapper.fft(4))[2];
        int c3 = (int)(MinimWrapper.fft(4))[3];

        for( int i = -width; i < width; i += width / 2) {
          for( int j = -height; j < height; j += height / 2) {
            g.setColor(new Color(0xA0, 0x0A, 0x00, 0x80));
	    g.setStroke(new BasicStroke(c0));
            g.drawLine(i, j, -x, y + y);
            g.setColor(new Color(0xA0, 0x00, 0xA0, 0x80));
	    g.setStroke(new BasicStroke(c1));
            g.drawLine(i, j, -x, -y);
            g.setColor(new Color(0x00, 0xA0, 0xA0, 0x80));
	    g.setStroke(new BasicStroke(c2));
            g.drawLine(i, j, x + x, -y);
            g.setColor(new Color(0xA0, 0xA0, 0xA0, 0x80));
	    g.setStroke(new BasicStroke(c3));
            g.drawLine(i, j, x + x, y + y);
	    g.setStroke(new BasicStroke(1f));
          }
        }
    }
}
