package hypernova.pilots;

import hypernova.Mass;
import hypernova.Ship;
import hypernova.ActivitySimple;
import hypernova.DestructionListener;

public abstract class EventPilot extends Pilot 
                                 implements DestructionListener {
    protected ActivitySimple eventListener = null;
    protected int event = 0;
    protected String args = "";

    public EventPilot(Ship ship, ActivitySimple listener, int event) {
        super(ship);
        eventListener = listener;
        this.event = event;
    }

    public void setShip(Ship ship) {
      ship.onDestruct(this);
      super.setShip(ship);
    }

    public void destroyed(Mass m) {
       eventListener.setTimeout(event, 1, args);
    }
}
