package hypernova.gui;

import java.awt.event.KeyEvent;

import hypernova.Universe;
import hypernova.Hypernova;
import java.awt.Graphics2D;
import java.awt.Graphics;

public class Menu
{
    private static MenuScreen screen = null;
    private static boolean inMenu = false;
    public static boolean inMenu() { return inMenu; }
    public static void setInMenu(boolean x) { inMenu = x; }
 
    public static void render(Graphics2D g2d) {
      screen.render(g2d);
    }

    public static void begin(MenuScreen m) {
      screen = m;
      inMenu = true;
      Transition.startTransition(Transition.Types.MENU_IN);
      m.loadMenu();
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
