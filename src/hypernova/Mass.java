package hypernova;

import java.util.Random;
import java.awt.Shape;

import hypernova.gui.Model;

public class Mass {
    public static final double BREAKUP_SPEED = 0.25;
    public static final double BREAKUP_ANGLE = 0.01;
    public static final double BREAKUP_TTL = 100;
    public static final double BREAKUP_TTL_VAR = 40;

    private static final Random rng = new Random();

    /* State vectors -- {pos, vel, acc}. */
    protected double[] x = new double[3];
    protected double[] y = new double[3];
    protected double[] a = new double[3];
    protected Hull hull;
    private double size = 5.0;
    private Faction faction;
    protected boolean shortlived;
    protected int ttl;

    protected Mass() {
    }

    public Mass(Hull hull) {
        this.hull = hull;
        setSize(size);
        faction = Faction.getDefault();
    }

    public Mass(String hullname) {
        this(Hull.get(hullname));
    }

    public void step(double t) {
        if (shortlived && ttl-- < 0) {
            zenThing();
            return;
        }

        x[1] += x[2] * t;
        y[1] += y[2] * t;
        a[1] += a[2] * t;
        x[0] += x[1] * t;
        y[0] += y[1] * t;
        a[0] += a[1] * t;
        while (a[0] < -Math.PI) a[0] += Math.PI * 2;
        while (a[0] > Math.PI) a[0] -= Math.PI * 2;
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

    public void damage(double val) {
        double hp = hull.getHP() - val;
        if (hp <= 0) {
            destruct();
            return;
        }
        hull.setHP(hp);
    }

    public void destruct() {
        Model[] models = hull.getModel().breakup();
        Hull[] hulls = new Hull[models.length];
        for (int i = 0; i < models.length; i++) {
            Mass m = new Mass(new Hull(models[i]));
            m.setSize(size);
            m.setPosition(this);
            m.x[1] = x[1] + rng.nextGaussian() * BREAKUP_SPEED;
            m.y[1] = y[1] + rng.nextGaussian() * BREAKUP_SPEED;
            m.a[1] = a[1] + rng.nextGaussian() * BREAKUP_ANGLE;
            m.shortlived = true;
            m.ttl = (int) (BREAKUP_TTL + rng.nextGaussian() * BREAKUP_TTL_VAR);
            m.setFaction(faction);
            Hypernova.universe.add(m);
        }
        zenThing();
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
