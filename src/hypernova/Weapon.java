package hypernova;

public class Weapon {
    private double speed;
    private double rate, timeout;

    public Weapon(double speed, double rate) {
        this.speed = speed;
        this.rate = rate;
    }

    public void fire(Universe u, Mass src) {
        if (timeout <= 0) {
            Mass shot = new Shot(u, src.getX(0), src.getY(0), src.getA(0),
                                 "shot", speed);
            shot.addX(src.getX(1), 1);
            shot.addY(src.getY(1), 1);
            u.add(shot);
            timeout = rate;
        }
    }

    public void step(double t) {
        timeout -= t;
    }
}
