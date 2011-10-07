package hypernova.pilots;

import hypernova.Ship;
import hypernova.Universe;
import hypernova.MinimWrapper;
import hypernova.Mass;
import hypernova.DestructionListener;
import hypernova.activities.ChuckToTheFuture;

public class Beamer extends Hunter implements DestructionListener{
    public Beamer(Ship ship) {
        super(ship, null);
    }

    public void setShip(Ship ship) {
      ship.onDestruct(this);
      super.setShip(ship);
    }

    public void destroyed(Mass m)
    {
      ChuckToTheFuture.shipDead();
    }
   
    public void fireShots(Ship ship)
    {
      int max = 0;
      if(MinimWrapper.fft(4)[1] > MinimWrapper.fft(4)[max]) max = 1;
      if(MinimWrapper.fft(4)[2] > MinimWrapper.fft(4)[max]) max = 2;
      if(MinimWrapper.fft(4)[3] > MinimWrapper.fft(4)[max]) max = 3;
      if(max != 0 || MinimWrapper.fft(4)[0] > 10) ship.fire(max);
    }

    private Ship getPlayer() {
        return Universe.get().getPlayer();
    }

    public void drive(double dt) {
        if (target == null || !target.isActive()) target = getPlayer();
        super.drive(dt);
    }
}
