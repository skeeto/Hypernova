package hypernova.activities;

import java.util.ArrayList;
import java.util.Iterator;
import hypernova.Ship;
import hypernova.Universe;
import hypernova.ActivitySimple;
import hypernova.gui.Viewer;
import hypernova.gui.Info;

public class CountDown extends ActivitySimple {
    private int countDown = 10;
    private int counter   = 15;
 
    private ArrayList<Ship> ss = new ArrayList<Ship>();
   
    private void newShip(int xOff, int yOff)
    {
        Ship player = Universe.get().getPlayer();
        Ship s = addShip("drone", "Invaders", PilotType.EVENT_DRONE, player.getX(0) + xOff, player.getY(0) + yOff);
        ss.add(s);
    }

    public void eventHandler(int event, String eventArgs)
    { 
      if(counter <= 0) return;
      if(event == 0) /* Timer event */
      {
        countDown --;
        if(countDown == 0)
        { 
          u.queueMessage("Incoming!");
          newShip(100,100);
          newShip(-100,100);
          newShip(-100,-100);
          newShip(100,-100);
          countDown = 10;
        }
        Info.timeLeft = countDown;
        setTimeout(0, 1000);
      } else if (event == ActivitySimple.SHIP_DESTROY) {
        counter --;
        Info.shipsLeft = counter;
        if( counter <= 0 )
        {
          u.queueMessage("Guess they've had enough");
          hypernova.universes.Test.setCountDone();
          Info.visibleTimer = false;
          Info.visibleCounter = false;

          Iterator<Ship> iterator = ss.iterator();
          while(iterator.hasNext()) iterator.next().destruct();

          this.finish();
        }
      }
    }

    public void begin(double px, double py) {
        /* Set up info box */
        Info.timeLeft = countDown;
        Info.visibleTimer = true;
        Info.shipsLeft = counter;
        Info.visibleCounter = true;

        u.queueMessage("That timer seems a tad ominous");
        u.queueMessage("I'm sure it will be fine");
        setTimeout(0, 1000);
    }

}
