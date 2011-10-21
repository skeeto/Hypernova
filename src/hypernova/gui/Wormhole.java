package hypernova.gui;

import java.awt.*;
import java.awt.image.*;
import java.lang.Math;
import java.awt.geom.AffineTransform;

import hypernova.MinimWrapper;

public class Wormhole
{
   double[] c = new double[8];
   int[] a = new int[4]; 

   public double calcVal(int n, int i, int j, float[] f)
   {
       switch(n)
       {
           case 0: 
               return  + 2 * c[0] * f[0] * j
                       - 10 * c[1] * f[1] * j * j 
                       - 10 * c[2] * f[2] * i * i
                       + 20* c[3] * f[3] * i * j 
                       + 20* c[4] * f[4] * i 
                       + 2* c[5] * Math.sin(f[5]*i) 
                       + 2* c[6] * Math.cos(f[6]) * i 
                       + 2*c[7] * f[7];
           case 1:
               return - c[0] * f[0] * j
                      - c[1] * f[1] * j 
                      - c[2] * f[2] * i
                      - c[3] * f[3] * i 
                      + c[4] * f[4] * i * i 
                      + c[5] * f[5] * j * j 
                      + c[6] * Math.sin(f[6]) 
                      - c[7] * Math.cos(f[7]);  
       }    
       return 0;
   }

   public void setColors(int n, int v, int i, int j)
   {
       switch(n)
       {
           case 0:
               a[0] = 0; 
               a[1] = (v*2) % 255; 
               a[2] = (v*3)%255;
               a[3] = a[1] + a[2]; 
               if( v < 10) a[3] = 0;
               break;
           case 1:
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
      }
   }

   public void setConsts(int n)
   {
       switch(n)
       {
       case 0:
           c[0] = 4.9355043E-5;
           c[1] = 1.1944349E-5;
           c[2] = 9.123675E-6;
           c[3] = 2.3133858E-5;
           c[4] = 3.9011925E-6;
           c[5] = 2.7231476E-6;
           c[6] = 2.9558503E-5;
           c[7] = 1.1900892E-5; 
           break;
       case 1:    
           c[0] = 1;
           c[1] = 1;
           c[2] = 1;
           c[3] = 1;
           c[4] = 1;
           c[5] = 1;
           c[6] = 1;
           c[7] = 1;
           break;
       } 
   }

   public void draw(Graphics2D g2d, double s, int w, int h, int x, int y, int n)
   {
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
      AffineTransform at = new AffineTransform();
      if(s <= 1.25) at.scale(0.1, 0.1);
      else if(s > 2.5) at.scale(1.25, 1.25);
      else at.scale(s - 1.25, s - 1.25);

      BufferedImageOp bio;
      bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
      BufferedImage IP = bio.filter(I, null);
      g2d.drawImage(IP, x*(int)s, y*(int)s, null);
   }
}
