package hypernova.gui;

import java.awt.event.KeyEvent;

import hypernova.Universe;
import hypernova.Hypernova;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class Menu
{
    private static BufferedImage img = null;
    private static BufferedImage screenshot = null;
    private static MenuScreen screen = null;
    private static boolean clearScreen = false;
    private static boolean inMenu = false;
    private static boolean keepBg = true;
    public static boolean inMenu() { return inMenu; }
    public static boolean keepBg() { return keepBg; }
    public static void setInMenu(boolean x) { inMenu = x; }
    public static void setKeepBg(boolean x) { keepBg = x; }
 
    public static void render(Graphics2D g2d) {
      if(clearScreen)
      {
        g2d.drawImage(img, 0, 0, null);
        clearScreen = false;
      }
      screen.render(g2d);
    }

    public static void newMenu(MenuScreen m, boolean doLoad) {
      screen = m;
      clearScreen = true;
      if(doLoad) m.loadMenu();
    }

    public static void begin(MenuScreen m) {
      Menu.screenshot = Hypernova.getViewer().getImage();
      screen = m;
      inMenu = true;
      Transition.startTransition(Transition.Types.MENU_IN);
    }

    public static void load(BufferedImage img) {
      Menu.img = img;
      screen.loadMenu();
    }

    public static BufferedImage getScreenshot() {
      return screenshot;
    }

    public static void handleKeys(int k) {
      switch(k)
      {
        case KeyEvent.VK_ESCAPE:
          screen.back();
          break;
        case KeyEvent.VK_ENTER:
          screen.select();
          break;
        case KeyEvent.VK_UP:
          screen.goUp();
          break;
        case KeyEvent.VK_DOWN:
          screen.goDown();
          break;
      }
    } 
}
