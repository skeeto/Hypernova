package hypernova.gui;

import java.awt.event.KeyEvent;

import hypernova.Universe;
import hypernova.Hypernova;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Movie
{
    private static String movie = "";
    private static int frame = 1;
    private static int w = 0;
    private static int h = 0;
    private static boolean inMovie = false;
    public static boolean inMovie() { return inMovie; }
    public static void setInMovie(boolean x) { inMovie = x; }

    public static void render(Graphics2D g2d) {
      File f;
      BufferedImage img = null;
      try {
        f = new File("movies/" 
                    + movie 
                    + "/" + "/image" 
                    + (frame++) + ".png"
                    );
       img = ImageIO.read(f);
     } catch (Exception e) {
       inMovie = false;
       Universe.get().togglePause(false);
     }
     g2d.drawImage(img, 0, 0, w, h, null);
    }

    public static void begin(String m) {
      movie = m;
      w = Hypernova.getViewer().getWidth();
      h = Hypernova.getViewer().getHeight();
      inMovie = true;
      Transition.startTransition(Transition.Types.MENU_IN);
      frame = 1;
    }
}
