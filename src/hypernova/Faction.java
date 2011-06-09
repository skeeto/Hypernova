package hypernova;

import java.awt.Color;
import java.util.Map;
import java.util.HashMap;

public class Faction {
    private static Map<String, Faction> store = new HashMap<String, Faction>();
    private static Faction def;

    private Color color;
    private String name;

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

    public Faction(String name, Color color) {
        this.name = name;
        this.color = color;
        store.put(name, this);
        if (def == null)
            def = this;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
