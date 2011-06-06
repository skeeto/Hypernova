package hypernova;

public class Shot extends Mass {
    public static final int DEFAULT_TTL = 200;
    private int ttl;

    public Shot(Universe u, double x, double y, double a,
                double speed, int ttl) {
        super(u, x, y, a);
        setX(Math.cos(a) * speed, 1);
        setY(Math.sin(a) * speed, 1);
        this.ttl = ttl;
    }

    public Shot(Universe u, double x, double y, double a, double speed) {
        this(u, x, y, a, speed, DEFAULT_TTL);
    }

    public void step(double t) {
        super.step(t);
        if (ttl-- < 0)
            universe.remove(this);
    }
}
