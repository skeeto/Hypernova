package hypernova;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;


public class ActivityEvents {
    private static final int CYCLESPEED = 40; // Time in milliseconds
   // private static ArrayList<ActivityEvents> events = new ArrayList<ActivityEvents>();
    private static List<ActivityEvents> events = Collections.synchronizedList(new ArrayList<ActivityEvents>());
    private  ActivitySimple activity;
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
        Iterator i = events.iterator();
        while(i.hasNext())
        {
          ActivityEvents n = (ActivityEvents)i.next();
          if (n.cyclesLeft <= ActivityEvents.CYCLESPEED)
          {
              n.activity.eventHandler(n.eventNum, n.args);
              events.remove(n);
          } else {
              n.cyclesLeft -= ActivityEvents.CYCLESPEED;
          }
          
        }
      }
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
