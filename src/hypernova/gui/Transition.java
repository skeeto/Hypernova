package hypernova.gui;

import hypernova.Hypernova;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Transition
{
    public enum Types { NONE
                      , DIAGONAL
		      , BLOCKING
                      , FADE
                      };

    private static BufferedImage img = null;
    private static int transCount = 0;
    private static Types transType = Types.NONE;
    private static Viewer v = null; 
 
    public static void setViewer(Viewer view)
    {
        v = view;
    }
  
    public static void startTransition(Types t)
    {
        img = new BufferedImage(v.getWidth(), v.getHeight(),
                                BufferedImage.TYPE_INT_ARGB);
        Graphics gImg = img.getGraphics();
        v.paintComponent(gImg);
        gImg.dispose();
        transCount = 0;
        transType = t;
    }
  
    public static void doTransition(Graphics2D g2d)
    { 
        switch (transType)
        {
            case NONE:     break;
            case DIAGONAL: diagonal(g2d); break;
            case FADE:     fade(g2d); break;
        }
    }
   
    private static void diagonal(Graphics2D g2d)
    {
        int w = v.getWidth();
        int h = v.getHeight();
        g2d.drawImage(img, transCount *(w/50), transCount*(h/50), null);
        if(transCount ++ == 50) transType = Types.NONE;
    }

    private static void fade(Graphics2D g2d)
    {
       int[] a = new int[4]; 
       WritableRaster wr = img.getRaster();

       for (int y = 0; y < img.getHeight(); y++) 
       {
           for (int x = 0; x < img.getWidth(); x++) 
           {
               wr.getPixel(x,y,a);
               a[3] = 255 - 5*transCount; 
               wr.setPixel(x, y, a);
           }
       }
       g2d.drawImage(img, 0, 0, null);
       if(transCount ++ == 51) transType = Types.NONE;
    }
}
