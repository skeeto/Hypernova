package hypernova.gui.menus;

import hypernova.gui.MenuScreen;
import hypernova.gui.Menu;
import hypernova.Universe;

public class Pause extends MenuScreen
{
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
