package hypernova;

public class Mass {
    private double x, y, a;
    private double xdot, ydot, adot;

    public Mass(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.a = angle;
    }

    public Mass(double x, double y) {
        this(x, y, 0.0);
    }

    public void step(double t) {
        x += xdot * t;
        y += ydot * t;
        a += adot * t;
    }

    /* Getters and setters. */

    public void setX(double val) {
        x = val;
    }

    public void setY(double val) {
        y = val;
    }

    public void setA(double val) {
        a = val;
    }

    public void setXdot(double rate) {
        xdot = rate;
    }

    public void setYdot(double rate) {
        ydot = rate;
    }

    public void setAdot(double rate) {
        adot = rate;
    }

    public void addXdot(double rate) {
        xdot += rate;
    }

    public void addYdot(double rate) {
        ydot += rate;
    }

    public void addAdot(double rate) {
        adot += rate;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getA() {
        return a;
    }

    public double getXdot() {
        return xdot;
    }

    public double getYdot() {
        return ydot;
    }

    public double getAdot() {
        return adot;
    }
}
