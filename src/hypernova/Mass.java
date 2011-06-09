package hypernova;

import java.awt.Shape;

import hypernova.gui.Model;

public class Mass {
    /* State vectors -- {pos, vel, acc}. */
    protected double[] x = new double[3];
    protected double[] y = new double[3];
    protected double[] a = new double[3];
    protected Hull hull;
    private double size = 5.0;
    private Faction faction;

    protected Mass() {
    }

    public Mass(Hull hull) {
        this.hull = hull;
        setSize(size);
        faction = Faction.getDefault();
    }

    public Mass(String hullname) {
        this(Hull.getHull(hullname));
    }

    public void step(double t) {
        x[1] += x[2] * t;
        y[1] += y[2] * t;
        a[1] += a[2] * t;
        x[0] += x[1] * t;
        y[0] += y[1] * t;
        a[0] += a[1] * t;
        hull.getModel().transform(x[0], y[0], a[0]);
    }

    /** Remove oneself from the equation. */
    protected void zenThing() {
        Hypernova.universe.remove(this);
    }

    /* Getters and setters. */

    public Model getModel() {
        return hull.getModel();
    }

    public Hull getHull() {
        return hull;
    }

    public Shape getHit() {
        return getModel().getShapes()[0];
    }

    public double getMass() {
        return hull.getMass();
    }

    public Faction getFaction() {
        return faction;
    }

    public Mass setFaction(Faction faction) {
        this.faction = faction;
        return this;
    }

    public Mass setFaction(String name) {
        this.faction = Faction.get(name);
        return this;
    }

    public double getSize() {
        return size;
    }

    public Mass setSize(double val) {
        size = val;
        hull.getModel().setSize(val);
        return this;
    }

    public void setA(double val, int deriv) {
        a[deriv] = val;
    }

    public Mass setPosition(double px, double py, double pa) {
        x[0] = px;
        y[0] = py;
        a[0] = pa;
        return this;
    }

    public Mass setPosition(double px, double py) {
        x[0] = px;
        y[0] = py;
        return this;
    }

    public Mass setPosition(Mass src) {
        x[0] = src.x[0];
        y[0] = src.y[0];
        a[0] = src.a[0];
        return this;
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
