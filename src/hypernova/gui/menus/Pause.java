package hypernova.gui.menus;

import hypernova.gui.MenuScreen;
import hypernova.gui.Menu;
import hypernova.gui.Viewer;
import hypernova.Universe;
import hypernova.Hypernova;
import java.io.File;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;

public class Pause extends MenuScreen
{
  private static Logger log = Logger.getLogger("gui.menus.Pause");
  private BufferedImage img;

  public Pause() {
    // Screenshot
    Viewer v = Hypernova.getViewer();
    img = new BufferedImage(v.getWidth(), v.getHeight(),
                            BufferedImage.TYPE_INT_ARGB);
    Graphics gImg = img.getGraphics();
    v.paintComponent(gImg);
    try { ImageIO.write(img, "PNG", new File("test.png"));}
    catch (Exception e) { log.error("Pause screenshot failed"); }

    gImg.dispose();
  }

  public void back() {
    Universe.get().togglePause(false);
    Menu.setInMenu(false);
  }

  public void loadMenu() {
    addItem(Alignment.CENTER, null, "Continue", null, 0, false);
    addItem(Alignment.CENTER, null, "Save", null, 1, false);
    addItem(Alignment.CENTER, null, "Exit", null, 2, false);
  }
 
  public void functions(int func, String value) {
    switch(func)
    {
      case 0:
        back();
        break;
      case 1:
        break;
      case 2:
        System.exit(0);
        break;
    }
  }
}
