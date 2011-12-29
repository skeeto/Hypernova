package hypernova;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ActivityEvents {
    private static final int CYCLESPEED = 40; // Time in milliseconds
    private static Collection<ActivityEvents> events = new ConcurrentLinkedQueue<ActivityEvents>();
    private ActivitySimple activity;
    private int eventNum = 0;
    private int cyclesLeft = 0;
    private String args = "";
    
    ActivityEvents(ActivitySimple as, int e, int c, String a)
    {
        activity = as;
        eventNum = e;
        cyclesLeft = c;
        args = a;
    }

    public static void loop()
    {
      synchronized(events)
      {
        for (ActivityEvents e : events)
        {
          if (e.cyclesLeft <= ActivityEvents.CYCLESPEED)
          {
              e.activity.eventHandler(e.eventNum, e.args);
              events.remove(e);
          } else {
              e.cyclesLeft -= ActivityEvents.CYCLESPEED;
          }
        }
      }
    }

    public static void clear()
    {
        synchronized(events) { events.clear(); }
       
    }

    public static void add(ActivitySimple act, int event, int time)
    {
        add(act, event, time, "");
    }

    public static void add(ActivitySimple act, int event, int time, String args)
    {
        synchronized(events) 
        {
            ActivityEvents e = new ActivityEvents(act, event, time, args);
            events.add(e); 
        }
    }
}
