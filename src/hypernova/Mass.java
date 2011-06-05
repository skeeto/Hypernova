package hypernova;

public class Mass {
    private double x, y, a;
    private double xdot, ydot, adot;
    private boolean solid;

    protected final Universe universe;

    public Mass(Universe universe, double x, double y, double angle) {
        this.universe = universe;
        this.x = x;
        this.y = y;
        this.a = angle;
    }

    public void step(double t) {
        x += xdot * t;
        y += ydot * t;
        a += adot * t;
    }

    /* Getters and setters. */

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean set) {
        solid = set;
    }

    public void setState(int x, int y, int angle) {
        this.x = x;
        this.y = y;
        this.a = angle;
    }

    public void setDot(int xdot, int ydot, int adot) {
        this.xdot = xdot;
        this.ydot = ydot;
        this.adot = adot;
    }

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
