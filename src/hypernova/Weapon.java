package hypernova;

import java.util.Properties;

public class Weapon {
    public static final double DEFAULT_SPEED = 1.5;
    public static final double DEFAULT_COOLDOWN = 3.0;

    private double speed;
    private double rate, timeout;

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
        weapon.speed = attempt(props, "speed", DEFAULT_SPEED);
        weapon.rate = attempt(props, "cooldown", DEFAULT_COOLDOWN);
        return weapon;
    }

    private static double attempt(Properties ps, String prop, double def) {
        String str = ps.getProperty(prop);
        if (str == null)
            return def;
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            System.out.println("invalid value");
            return def;
        }
    }

    protected Weapon() {
    }

    public void fire(Universe u, Mass src) {
        if (timeout <= 0) {
            Mass ammo = new Ammo(u, src.getX(0), src.getY(0), src.getA(0),
                                 "bolt", speed);
            ammo.addX(src.getX(1), 1);
            ammo.addY(src.getY(1), 1);
            u.add(ammo);
            timeout = rate;
            Sound.play(0, 40, 75);
        }
    }

    public void step(double t) {
        timeout -= t;
    }
}
