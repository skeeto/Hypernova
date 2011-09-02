package hypernova.pilots;

import hypernova.Ship;
import hypernova.Universe;
import hypernova.MinimWrapper;

public class Beamer extends Hunter{
    public Beamer(Ship ship) {
        super(ship, null);
    }
   
    public void fireShots(Ship ship)
    {
      if(MinimWrapper.fft(4)[0] > 10) ship.fire(1);
      else ship.fire(0);
    }

    private Ship getPlayer() {
        return Universe.get().getPlayer();
    }

    public void drive(double dt) {
        if (target == null || !target.isActive()) target = getPlayer();
        super.drive(dt);
    }
}
