package hypernova.pilots;

import hypernova.Mass;
import hypernova.Ship;
import hypernova.Universe;;
import hypernova.ActivitySimple;
import hypernova.audio.MinimWrapper;

public class BeatDetect extends EventPilot {
    protected Mass target = Universe.get().getPlayer();

    public BeatDetect (Ship ship, ActivitySimple listener, int event)
    {
      super(ship,listener,event);
    }

    @Override
    public void drive(double dt) {
      double a = MinimWrapper.fft(4)[3];
      double x = MinimWrapper.fft(4)[1];
      double y = MinimWrapper.fft(4)[2];
      double s = MinimWrapper.fft(4)[0];
      getShip().getHull().getModel().setSize(s);
      getShip().getHull().getModel().setAngleMod(a);
    }
}
