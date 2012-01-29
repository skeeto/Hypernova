package hypernova.gui.menus;

import hypernova.gui.MenuScreen;
import hypernova.gui.Menu;
import hypernova.gui.Info;
import hypernova.gui.Viewer;
import hypernova.SaveGame;
import hypernova.Universe;

public class Load extends MenuScreen
{
  public void loadMenu() {
    for(int i = 1; i <= 5; i ++)
      addItem(Alignment.LEFT, SaveGame.saveStats(i), null, i);
    
    addItem(Alignment.RIGHT, "Back", null, 6);
  }
 
  public void functions(int func, String value) {
    if(func <= 5)
    {
      Universe.get().togglePause(false);
      Menu.setInMenu(false);
      Viewer.showMinimap = true;
      Info.showInfo = true;
      Menu.setKeepBg(false);
      SaveGame.load(func);
    } else back();
  }
}
