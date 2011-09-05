package hypernova.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;

public abstract class Background {
    
    protected int width  = Viewer.WIDTH;
    protected int height = Viewer.HEIGHT;
    protected int quality = Viewer.QUALITY_DEFAULT; 
    protected double scale = Viewer.DEFAULT_SCALE;
 
    public void setScale(double s) { scale = s; }
    public void setDimensions(int w, int h)
    {
       width = w;
       height = h;
    }
     
    public abstract void drawBackground(Graphics g, Graphics2D g2d, double focusX, double focusY);
}
