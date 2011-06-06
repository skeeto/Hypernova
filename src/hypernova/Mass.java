package hypernova;

public class Mass {
    /* State vectors -- {pos, vel, acc}. */
    private double[] x = new double[3];
    private double[] y = new double[3];
    private double[] a = new double[3];
    private boolean solid;

    protected final Universe universe;

    public Mass(Universe universe, double px, double py, double angle) {
        this.universe = universe;
        x[0] = px;
        y[0] = py;
        a[0] = angle;
    }

    public void step(double t) {
        x[1] += x[2] * t;
        y[1] += y[2] * t;
        a[1] += a[2] * t;
        x[0] += x[1] * t;
        y[0] += y[1] * t;
        a[0] += a[1] * t;
    }

    /* Getters and setters. */

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean set) {
        solid = set;
    }

    public void setX(double val, int deriv) {
        x[deriv] = val;
    }

    public void setY(double val, int deriv) {
        y[deriv] = val;
    }

    public void setA(double val, int deriv) {
        a[deriv] = val;
    }

    public void addX(double val, int deriv) {
        x[deriv] += val;
    }

    public void addY(double val, int deriv) {
        y[deriv] += val;
    }

    public void addA(double val, int deriv) {
        a[deriv] += val;
    }

    public double getX(int deriv) {
        return x[deriv];
    }

    public double getY(int deriv) {
        return y[deriv];
    }

    public double getA(int deriv) {
        return a[deriv];
    }
}
