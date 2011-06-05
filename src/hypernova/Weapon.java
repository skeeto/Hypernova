package hypernova;

public class Weapon {
    private double speed;

    public Weapon(double speed) {
        this.speed = speed;
    }

    public void fire(Universe u, double x, double y, double a) {
        u.add(new Shot(u, x, y, a, speed));
    }
}
