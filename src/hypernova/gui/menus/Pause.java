package hypernova.gui.menus;

import hypernova.gui.MenuScreen;
import hypernova.gui.Menu;

public class Pause extends MenuScreen
{
  public void loadMenu() {
    addItem(Alignment.CENTER, null, "Exit", null, 0, false);
  }
 
  public void functions(int func, String value) {
    System.exit(0);
  }
}
