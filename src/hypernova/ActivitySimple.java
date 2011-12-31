package hypernova;

import java.util.Random;

import java.awt.Shape;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import java.awt.Color;
import hypernova.pilots.*;
import hypernova.gui.MapMarker;

public class ActivitySimple extends Activity implements Realization {
    public static final int SHIP_DESTROY = -1;
    protected static final Random RNG = new Random();
    
    protected double WIDTH  = 250;
    protected double HEIGHT = 250;
 
    protected Universe u = Universe.get();
    protected Color markerColor = new Color(255,0,0);
    protected MapMarker m;

    private double x, y;
    private Shape zone;

    public enum PilotType { BEAMER
                          , PLAYER_HUNTER
                          , CIRCLE_PILOT 
                          , EVENT_DRONE
                          , BEAT_DETECT 
                          };


    // Basic event handler to call in case of an action
    public void eventHandler(int event, String eventArgs)
    {
        log.debug("Activity Event: " + event + " " + eventArgs);
    }

    // Wait for a set amount of milleseconds and call the handler
    public void setTimeout(int event, int timeMillis)
    {
        setTimeout(event, timeMillis, "");
    }

    public void setTimeout(int event, int timeMillis, String args)
    {
        ActivityEvents.add(this,event,timeMillis,args);
    }
 
    public void finish()
    {
        if(m != null) m.setVisible(false);
    }
  
    public Ship addShip(String design, String faction, PilotType pilot, 
                        double sx, double sy)
    {
        return addShip(design, faction, pilot, sx, sy, SHIP_DESTROY);
    }

    public Ship addShip(String design, String faction, PilotType pilot, 
                        double sx, double sy, int event)
    {
        Ship s = Ship.get(design);
        s.setFaction(faction);
        s.setPosition(sx, sy);
        Pilot p = null;
        switch(pilot)
        {
           case BEAMER:
              p = new Beamer(s);
              break;
           case PLAYER_HUNTER:
              p = new PlayerHunter(s);
              break;
           case CIRCLE_PILOT:
              p = new CirclePilot(s, 10);
              break;
           case EVENT_DRONE:
              p = new EventDrone(s, this, event);
              break;
           case BEAT_DETECT:
              p = new BeatDetect(s, this, event);
              break;
           default:
              log.error("Invalid Ship");
        }
        p.setShip(s);
        s.setPilot(p);
        (Universe.get()).add(s);
        return s;
    }

    // On added to world
    public void realize(double x, double y) {
        this.x = x;
        this.y = y;
        m = new MapMarker(x,y,markerColor,true);
        MapMarker.add(m);
        zone = new Rectangle2D.Double(x - WIDTH, y - HEIGHT,
                                      WIDTH * 2, HEIGHT * 2);
        Universe.get().add(this);
    }

    // Not started
    public boolean shouldTrigger(double x, double y) {
        return zone.contains(x, y);
    }

    // Start
    public void trigger(double px, double py) {
        u.remove(this);
        this.begin(px, py);
    }
    
    public void begin(double px, double py) {
        log.debug("Activity Triggered");
    }

}
