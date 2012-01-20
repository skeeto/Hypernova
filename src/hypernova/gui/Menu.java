package hypernova.gui;

import java.awt.event.KeyEvent;
import javax.swing.JFileChooser;

import hypernova.Universe;
import hypernova.Hypernova;


// align : img: name : function

public class Menu
{
    private static boolean inMenu = false;
 
    public static boolean inMenu() { return inMenu; }
   
    public static void begin() {
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
        case KeyEvent.VK_F:
          JFileChooser fc = new JFileChooser();
          int returnVal = fc.showOpenDialog(Hypernova.getViewer());
          break;
      }
    } 
}
