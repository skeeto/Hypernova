package hypernova.gui;

import java.awt.event.KeyEvent;

import hypernova.Universe;
import hypernova.Hypernova;

public class Menu
{
    private static MenuScreen topMenu = null;
    private static boolean inMenu = false;
    public static boolean inMenu() { return inMenu; }
   
    public static void begin(MenuScreen m) {
      topMenu = m;
      inMenu = true;
      Transition.startTransition(Transition.Types.MENU_IN);
    }

    public static void handleKeys(int k) {
      switch(k)
      {
        case KeyEvent.VK_ESCAPE:
          Universe.get().togglePause(false);
          inMenu = false;
          break;
        case KeyEvent.VK_Q:
          System.exit(0);
          break;
      }
    } 
}
