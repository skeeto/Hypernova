package hypernova;

import java.util.Properties;

public class Weapon {
    public static final double DEFAULT_COOLDOWN = 3.0;
    public static final double DEFAULT_MASS = 1.0;
    public static final String DEFAULT_AMMO = "bolt";

    private double cooldown, timeout;
    private double mass;
    private Ammo ammo;

    public static Weapon getWeapon(String name) {
        String filename = "parts/" + name + ".weapon";
        Properties props = new Properties();
        try {
            props.load(Weapon.class.getResourceAsStream(filename));
        } catch (java.io.IOException e) {
            /* TODO handle this more gracefully. */
            return null;
        }

        Weapon weapon = new Weapon();
        weapon.cooldown = attempt(props, "cooldown", DEFAULT_COOLDOWN);
        weapon.mass = attempt(props, "mass", DEFAULT_MASS);
        String ammoname = props.getProperty("ammo");
        if (ammoname == null)
            ammoname = DEFAULT_AMMO;
        weapon.ammo = Ammo.getAmmo(ammoname);
        return weapon;
    }

    public static double attempt(Properties ps, String prop, double def) {
        String str = ps.getProperty(prop);
        if (str == null)
            return def;
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            System.out.println("invalid value for " + prop);
            return def;
        }
    }

    protected Weapon() {
    }

    public void fire(Mass src) {
        if (timeout <= 0) {
            Sound.play(0, 40, 75);
            Hypernova.universe.add(ammo.copy(src));
            timeout = cooldown;
        }
    }

    public void step(double t) {
        timeout -= t;
    }
}
