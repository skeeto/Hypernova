package hypernova.gui;

import java.awt.*;
import java.awt.image.*;
import java.lang.Math;

import hypernova.MinimWrapper;
public class Wormhole
{
   double[] c = new double[8];

   public void draw(Graphics2D g2d, int w, int h, int x, int y)
   {
      BufferedImage I = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      WritableRaster wr = I.getRaster();
      int[] a = new int[4]; 
     
      c[0] = 4.9355043E-5;
      c[1] = 1.1944349E-5;
      c[2] = 9.123675E-6;
      c[3] = 2.3133858E-5;
      c[4] = 3.9011925E-6;
      c[5] = 2.7231476E-6;
      c[6] = 2.9558503E-5;
      c[7] = 1.1900892E-5; 

      for (int i = 0; i < w/2; i ++)
      {
        for (int j = 0; j < h/2; j ++)
        {
            float f[] = (MinimWrapper.fft(8));
            double v  = 2 * c[0] * f[0] * j
                      - 10 * c[1] * f[1] * j * j 
                      - 10 * c[2] * f[2] * i * i
                      + 20* c[3] * f[3] * i * j 
                      + 20* c[4] * f[4] * i 
                      + 2* c[5] * Math.sin(f[5]*i) 
                      + 2* c[6] * Math.cos(f[6]) * i 
                      + 2*c[7] * f[7];
            int vp = (int) v;
            a[0] = 0; a[1] = (vp*2) % 255; a[2] = (vp*3)%255; a[3] = a[1] + a[2]; 
            if( v < 10) a[3] = 0;
            wr.setPixel(i, j, a);
            wr.setPixel(w - i - 1, w - j - 1, a);
            wr.setPixel(w - i - 1, j, a);
            wr.setPixel(i, w - j - 1, a);
        }
      }
      g2d.drawImage(I, x, y, null);
   }
}
