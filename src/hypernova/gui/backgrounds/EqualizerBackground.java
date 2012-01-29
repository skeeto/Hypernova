package hypernova.gui.backgrounds;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import hypernova.audio.MinimWrapper;
import hypernova.gui.Background;
import hypernova.gui.Viewer;
import java.lang.Math;

public class EqualizerBackground extends Background{
    public void drawBackground(Graphics g, Graphics2D g2d, double focusX, double focusY) {
        int bc = Math.min((int) MinimWrapper.fft(), 255);
        g.setColor(new Color(bc,Math.min(bc*2,255),Math.min(bc*3, 255)));
        g.fillRect(0, 0, width, height);
        for (int i = 0; i < 32; i ++ )
        {
            float x = (MinimWrapper.fft(32))[i];
            int c = Math.min( 2 * (int) x, 255);
            g.setColor(new Color(c,c,c));
            g.fillRect(i*(width/32), height/2, width/32, c);
            g.fillRect(i*(width/32), height/2 - c, width/32, c);
        }
    }
}
