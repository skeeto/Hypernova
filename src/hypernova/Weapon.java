package hypernova;

public class Weapon {
    private double speed;
    private double rate, timeout;

    public Weapon(double speed, double rate) {
        this.speed = speed;
        this.rate = rate;
    }

    public void fire(Universe u, double x, double y, double a) {
        if (timeout <= 0) {
            u.add(new Shot(u, x, y, a, speed));
            timeout = rate;
        }
    }

    public void step(double t) {
        timeout -= t;
    }
}
