package hypernova.gui;

import hypernova.Hypernova;
import hypernova.Universe;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;

public class Transition
{
    public enum Types { NONE
                      , DIAGONAL
		      , BLOCKING
                      , FADE
                      , FOUR
                      , MENU_IN
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
        img = v.getImage();
        transCount = 0;
        transType = t;
    }
  
    public static void doTransition(Graphics2D g2d)
    { 
        switch (transType)
        {
            case NONE:     break;
            case FOUR:     fourSquare(g2d); break;
            case DIAGONAL: diagonal(g2d); break;
            case FADE:     fade(g2d); break;
            case BLOCKING: blocking(g2d); break;
            case MENU_IN:  menu(g2d); break;
        }
    }
   
    private static void fourSquare(Graphics2D g2d)
    {
        int w = v.getWidth();
        int h = v.getHeight();
        g2d.drawImage(img, transCount *(w/50), transCount*(h/50), null);
        g2d.drawImage(img, -transCount *(w/50), transCount*(h/50), null);
        g2d.drawImage(img, transCount *(w/50), -transCount*(h/50), null);
        g2d.drawImage(img, -transCount *(w/50), -transCount*(h/50), null);
        if(transCount ++ == 50) transType = Types.NONE;
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

    private static void blocking(Graphics2D g2d)
    {
       int rW = img.getWidth()  / 5;
       int rH = img.getHeight() / 5;
       if(transCount < 30) g2d.drawImage(img, 0, 0, null);

       boolean[] blocks = new boolean[25];
       if(transCount < 30)
       {
         for(int i = 0; i < 25; i ++) blocks[i] = false;
         for(int i = 1; i <= transCount; i ++) 
           blocks[(7*(i + 1)) % 25] = true;
       } else {                      
         for(int i = 0; i < 25; i ++) blocks[i] = true;
         for(int i = 1; i <= (transCount - 26); i ++) 
           blocks[(11*(i + 1)) % 25] = false;
       }
       for(int i = 0; i < 25; i ++)
       {
          if(!blocks[i]) continue;
          int im = i % 5;
          int x = rW * im;
          int y = rH * (i - im) / 5; 
          Rectangle2D rect = new Rectangle2D.Double(x, y, rW, rH);
          g2d.setPaint(new Color(225,225,225,125));
          //g2d.draw(rect);
          g2d.fill(rect);
       }                         
       
       if(transCount ++ == 60) transType = Types.NONE;
    }

    private static void menu(Graphics2D g2d)
    {
       int[] a = new int[4]; 
       WritableRaster wr = img.getRaster();

       for (int y = 0; y < img.getHeight(); y++) 
       {
           for (int x = 0; x < img.getWidth(); x++) 
           {
               wr.getPixel(x,y,a);
               a[0] = 10*a[0] / 12;
               a[1] = 10*a[1] / 12;
               a[2] = 10*a[2] / 12;
               wr.setPixel(x, y, a);
           }
       }
       g2d.drawImage(img, 0, 0, null);
       if(transCount ++ == 10) {
         Universe.get().togglePause(true);
         Menu.load(img);
       } else if( transCount == 13 ) transType = Types.NONE;
    }
}
