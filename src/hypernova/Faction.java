package hypernova;

import java.awt.Color;
import java.util.Map;
import java.util.HashMap;

public class Faction {
    private static Map<String, Faction> store = new HashMap<String, Faction>();
    private static Faction def;

    private Color color;
    private String name;

    public static Faction create(String name, Color color) {
        Faction faction = new Faction(name, color);
        store.put(name, faction);
        if (def == null) def = faction;
        return faction;
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
        return color;
    }

    public String getName() {
        return name;
    }
}
