package hypernova.gui;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.lang.Math;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.Color;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import hypernova.audio.MinimWrapper;
import hypernova.Universe;
import hypernova.UniNames;
import hypernova.universes.*;

public class Wormhole
{
   public static boolean hold = false;

   private static Universe u = Universe.get();
   private static Collection<Wormhole> wormholes = new HashSet<Wormhole>();

   private double[] c = new double[8];
   private int[] a = new int[4]; 
   private int xr = 0;
   private int yr = 0;
   private int x = 0;
   private int y = 0;
   private int w = 0;
   private int h = 0;
   private UniNames n = UniNames.START;
   private Transition.Types t = Transition.Types.FADE;
  
   public static void add(int pX, int pY, int width, int height, 
                          UniNames name, Transition.Types tr)
   {
        synchronized(wormholes) 
        {
            wormholes.add(new Wormhole(pX, pY, width, height, name, tr)); 
        }
   }
 
   public static void clear()
   {
        synchronized(wormholes) { wormholes.clear(); }
   }

   public static void drawAll(Graphics2D g2d)
   {
        synchronized(wormholes) 
        {
            for (Wormhole w : wormholes) w.draw(g2d);
        }
   }

   private Wormhole(int pX, int pY, int width, int height, 
                    UniNames name, Transition.Types tr)
   {
       // 2* to compensate for scaling trickery
       x = 2*pX - width/2;
       y = 2*pY - height/2;

       xr = pX;
       yr = pY;
       w = width;
       h = height;
       n = name;
       t = tr;
       
       MapMarker.add(new MapMarker(xr,yr,new Color(255,255,255),true));
   }  

   public double calcVal(UniNames n, int i, int j, float[] f)
   {
       switch(n)
       {
           case START: 
               return  + 2 * c[0] * f[0] * j
                       - 10 * c[1] * f[1] * j * j 
                       - 10 * c[2] * f[2] * i * i
                       + 20* c[3] * f[3] * i * j 
                       + 20* c[4] * f[4] * i 
                       + 2* c[5] * Math.sin(f[5]*i) 
                       + 2* c[6] * Math.cos(f[6]) * i 
                       + 2*c[7] * f[7];
           case TEST:
               return - c[0] * f[0] * j
                      - c[1] * f[1] * j 
                      - c[2] * f[2] * i
                      - c[3] * f[3] * i 
                      + c[4] * f[4] * i * i 
                      + c[5] * f[5] * j * j 
                      + c[6] * Math.sin(f[6]) 
                      - c[7] * Math.cos(f[7]);
           case ALTER:
               return c[0]*(f[0] + 1)*i*i + c[0]*(f[1] + 1)*j*j;
           
       }    
       return 0;
   }

   public void setColors(UniNames n, int v, int i, int j)
   {
       switch(n)
       {
           case START:
               a[0] = 0; 
               a[1] = (v*2) % 255; 
               a[2] = (v*3)%255;
               a[3] = a[1] + a[2]; 
               if( v < 10) a[3] = 0;
               break;
           case TEST:
               if(v%2 == 0) a[0] = 255; 
               else a[0] = 0; 
               if(v%3 == 0) a[1] = 255; 
               else a[1] = 0; 
               if(v > 200) a[2] = 255; 
               else a[2] = 0; 
               if(v%57 == 0) a[3] = 255; 
               else a[3] = 0; 
               if(i < 100 && j < 100) a[3] = 0;
               break;
           case ALTER:
               a[0] = v%255;
               a[1] = 2*v%255;
               a[2] = 4*v%255;
               a[3] = 255;
               if( v % 2 == 0) a[3] = 0;
               break;
      }
   }

   public void setConsts(UniNames n)
   {
       switch(n)
       {
       case START:
           c[0] = 4.9355043E-5;
           c[1] = 1.1944349E-5;
           c[2] = 9.123675E-6;
           c[3] = 2.3133858E-5;
           c[4] = 3.9011925E-6;
           c[5] = 2.7231476E-6;
           c[6] = 2.9558503E-5;
           c[7] = 1.1900892E-5; 
           break;
       case TEST:    
           c[0] = 0.05;
           c[1] = 0.01;
           c[2] = 0.01;
           c[3] = 0.01;
           c[4] = 0.01;
           c[5] = 0.01;
           c[6] = 0.01;
           c[7] = 0.01;
           break;
       case ALTER:
           c[0] = 0.01;
           break;
       } 
   }

   private boolean checkBounds()
   {
      double px = u.getPlayer().getX(0);
      double py = u.getPlayer().getY(0);
      if( hold ) return true; 

      // Check if outside of viewable area
      if(  px < xr - 650 
        || px > xr + 650 
        || py < yr - 650 
        || py > yr + 650) return true;
         

      // Check if warp
      if( px > xr - 50 && px < xr + 50 && py > yr - 50 && py < yr + 50)
      { 
         hold = true;
         Transition.startTransition(t);
         Wormhole.clear();
         switch(n)
         {
             case TEST:
                 u.loadUniverse(Test.INSTANCE);
                 break;
             case START:
                 System.out.println("START: " + Start.INSTANCE);
                 u.loadUniverse(Start.INSTANCE);
                 break;
             case ALTER:
                 System.out.println("ALTER: " + Alter.INSTANCE);
                 u.loadUniverse(Alter.INSTANCE);
                 break;
         }
         return true;
      } 
      return false;
   }

   public void draw(Graphics2D g2d)
   {
      if(checkBounds()) return;

      BufferedImage I = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      WritableRaster wr = I.getRaster();
  
      setConsts(n);   

      for (int i = 0; i < w/2; i ++)
      {
        for (int j = 0; j < h/2; j ++)
        {
            float f[] = (MinimWrapper.fft(8));
            double v  = calcVal(n,i,j,f);
            setColors(n, (int) v, i, j);
            wr.setPixel(i, j, a);
            wr.setPixel(w - i - 1, h - j - 1, a);
            wr.setPixel(w - i - 1, j, a);
            wr.setPixel(i, h - j - 1, a);
        }
      }
      g2d.drawImage(I, x, y, null);
   }
   
}
