package hypernova;

public class Ammo extends Mass {
    public static final int DEFAULT_TTL = 200;
    private int ttl;

    public Ammo(Universe u, double x, double y, double a, String model,
                double speed, int ttl) {
        super(u, x, y, a, model);
        setX(Math.cos(a) * speed, 1);
        setY(Math.sin(a) * speed, 1);
        this.ttl = ttl;
    }

    public Ammo(Universe u, double x, double y, double a, String model,
                double speed) {
        this(u, x, y, a, model, speed, DEFAULT_TTL);
    }

    public void step(double t) {
        super.step(t);
        if (ttl-- < 0)
            universe.remove(this);
    }
}
