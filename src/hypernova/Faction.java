package hypernova;

import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import hypernova.audio.MinimWrapper;

public class Faction {
    private static Map<String, Faction> store = new HashMap<String, Faction>();
    private static Faction def;

    private Color color;
    private String name;

    public enum ColorType { TEST_HUMAN
                          , TEST_INVADER
                          }

    private static class MusicColor extends Color {
        static final long serialVersionUID = 1328933172837495L;  
        private ColorType type;
        public MusicColor(ColorType t){super(0,0,0); type = t;}
   
        public Color get(){
          float f[] = MinimWrapper.fft(4);
          float m[] = MinimWrapper.max();
          Color ret = null;
          switch(type)
          {
              case TEST_HUMAN:
              case TEST_INVADER:
                  int x = (int) ( 95 * f[0] / m[0] );
                  int r = (int) ( 95 * f[1] / m[1] );
                  int g = (int) ( 95 * f[2] / m[2] );
                  int b = (int) ( 95 * f[3] / m[3] );
                  if( type == ColorType.TEST_HUMAN)
                      ret = new Color( r + x, 255 - g - x, 255 - b - x);
                  else 
                      ret = new Color( 255 - r - x, g + x, 255 - b - x);
                  break;
          }
          return ret;
        }
    }

    public static Faction create(String name, ColorType t) {
       return Faction.create(name, new MusicColor(t));
    }

    public static Faction create(String name, Color color) {
        Faction faction = new Faction(name, color);
        store.put(name, faction);
        if (def == null) def = faction;
        if("Humans".equals(name))
        {
            Universe u = Universe.get();
            if(u != null) u.getPlayer().setFaction("Humans");
        }
        return faction;
    }

    public static void clear()
    {
        store.clear();
    }

    public static Faction get(String name) {
        Faction faction = store.get(name);
        if (faction == null) {
            return def;
        } else {
            return faction;
        }
    }

    public static Faction getDefault() {
        return def;
    }

    private Faction(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        if(color instanceof MusicColor) return ((MusicColor)color).get();
        else return color;
    }

    public String getName() {
        return name;
    }
}
