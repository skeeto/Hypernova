package hypernova;

import java.util.LinkedList;
import java.util.Collection;
import java.util.Random;
import java.awt.Shape;
import java.awt.geom.Area;

import hypernova.gui.Model;
import hypernova.audio.MinimWrapper;

public class Mass {
    public static final double BREAKUP_SPEED = 0.25;
    public static final double BREAKUP_ANGLE = 0.01;
    public static final double BREAKUP_TTL = 100;
    public static final double BREAKUP_TTL_VAR = 40;

    public static final double THRUST_SPEED = 15;
    public static final double THRUST_SPEED_VAR = 5;
    public static final double SPARK_SPEED = 60;
    public static final double SPARK_SPEED_VAR = 25;
    public static final int SPARK_TTL = 20;
    public static final double SPARK_DRAG = 5;

    public static final double DRAG = 0.005;

    private static final Random RNG = new Random();

    private static int idCount = 0;

    /* State vectors -- {pos, vel, acc}. */
    public final int ID;
    private boolean active;
    protected double[] x = new double[3];
    protected double[] y = new double[3];
    protected double[] a = new double[3];
    protected double hp;
    protected double batt;
    protected double energyRegen;
    protected Hull hull;
    private double size;
    private Faction faction;
    protected boolean shortlived;
    protected boolean suffersdrag = true;
    protected int ttl;
    private final Collection<DestructionListener> listeners
    = new LinkedList<DestructionListener>();

    protected Mass() {
        this(createID());
    }

    protected Mass(int id) {
        ID = id;
    }

    public Mass(Hull hull) {
        this();
        this.hull = hull;
        setSize(hull.getSize());
        hp = hull.getHP();
        batt = hull.getBatt();
        energyRegen = hull.getEnergyRegen();
        faction = Faction.getDefault();
    }

    public Mass(String hullname) {
        this(Hull.get(hullname));
    }

    private static synchronized int createID() {
        return idCount++;
    }

    public void step(double t) {
        if (shortlived && ttl-- < 0) {
            zenThing();
            return;
        }

        /* Regenerate Energy */
        if(energyRegen > 0) batt += energyRegen;
        if(batt > getMaxBatt()) batt = getMaxBatt();

        /* Add drag. */
        if (suffersdrag) {
            double v = Math.sqrt(x[1] * x[1] + y[1] * y[1]);
            double a = Math.atan2(y[1], x[1]);
            double d = drag(v);
            x[1] += d * -Math.cos(a) * t;
            y[1] += d * -Math.sin(a) * t;
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

        if(!shortlived) {
          int accVector =(int) (25 * ( Math.abs(x[2]) + Math.abs(y[2]) ));
          if(accVector > 1) thrust(accVector);
        }

        for (Mass m : Universe.get().getObjects()) {
          if ( !m.shortlived && m.isActive() && m != this
               && this.getHit().intersects(m.getHit().getBounds2D())) {
            Area a = new Area(this.getHit());
            a.intersect(new Area(m.getHit()));
            if(!a.isEmpty()) {
              this.collision(m);
            }
          }
        }
    }

    protected void collision(Mass m) {
                 x[1] = -x[1];
                 y[1] = -y[1];
    }

    private double drag(double v) {
        return v * v * DRAG * hull.getDrag() / getMass();
    }

    /** Remove oneself from the equation. */
    protected void zenThing() {
        Universe.get().remove(this);
    }

    /* Getters and setters. */

    public Model getModel() {
        return hull.getModel();
    }

    public Hull getHull() {
        return hull;
    }

    public Shape getHit() {
        return getModel().getHit();
    }

    public boolean useEnergy(double val) {
        if (batt >= val) {
          batt -= val;
          return true;
        } 
        return false;
    } 

    public void damage(double val) {
        hp -= val;
        if (hp <= 0) {
            destruct();
            return;
        } else if(hp >= getMaxHP()) {
            hp = getMaxHP();
        }
    }

    public void destruct() {
        Model[] models = hull.getModel().breakup();
        Hull[] hulls = new Hull[models.length];
        if(this instanceof Loot) 
        {
          zenThing();
        } else {
          Universe u = Universe.get();
          if(u.getPlayer().getHull() == hull) {
             u.queueMessage("ABSOLUTE DEATH");
             u.queueMessage("Press 'L' to revive");
          }
          if(!"".equals(hull.getDestroySound())) MinimWrapper.playSoundAsync(hull.getDestroySound());
          if(hull.getExplosionSize() == 0)
          { 
            for (int i = 0; i < models.length; i++) {
              Mass m = new Mass(new Hull(models[i]));
              m.setSize(size);
              m.setPosition(this);
              m.x[1] = x[1] + RNG.nextGaussian() * BREAKUP_SPEED;
              m.y[1] = y[1] + RNG.nextGaussian() * BREAKUP_SPEED;
              m.a[1] = a[1] + RNG.nextGaussian() * BREAKUP_ANGLE;
              m.shortlived = true;
              m.ttl = (int) (BREAKUP_TTL + RNG.nextGaussian() * BREAKUP_TTL_VAR);
              m.setFaction(faction);
              Universe.get().add(m);
            }
            zenThing();
          } else explode(hull.getExplosionSize());
        }

        for (DestructionListener listener : listeners) {
            listener.destroyed(this);
        }

    }


    public void thrust(int count) {
        Universe u = Universe.get();
        for(int i = 0; i < count; i++) {
            Mass spark = new Mass(new Hull(Model.get("spark")));
            spark.setPosition(this).setFaction(getFaction());
            double ap = a[0] + 7*Math.PI/8 + RNG.nextDouble() * Math.PI / 4;
            spark.a[0] = ap;
            double speed = THRUST_SPEED + RNG.nextGaussian() * THRUST_SPEED_VAR;
            spark.x[1] = Math.cos(ap) * speed;
            spark.y[1] = Math.sin(ap) * speed;
            spark.x[0] += spark.x[1];
            spark.y[0] += spark.y[1];
            spark.hull.setDrag(SPARK_DRAG);
            spark.suffersdrag = false;
            spark.shortlived = true;
            spark.ttl = -1;
            u.add(spark);
        }
    }

    public void explode(int count) {
        Universe u = Universe.get();
        for(int i = 0; i < count; i++) {
            Mass spark = new Mass(new Hull(Model.get("spark")));
            spark.setPosition(this).setFaction(getFaction());
            double a = RNG.nextDouble() * Math.PI * 2;
            spark.a[0] = a;
            double speed = SPARK_SPEED + RNG.nextGaussian() * SPARK_SPEED_VAR;
            spark.x[1] = Math.cos(a) * speed;
            spark.y[1] = Math.sin(a) * speed;
            spark.hull.setDrag(SPARK_DRAG);
            spark.suffersdrag = true;
            spark.shortlived = true;
            spark.ttl = SPARK_TTL;
            u.add(spark);
        }
        zenThing();
    }

    public void onDestruct(DestructionListener listener) {
        listeners.add(listener);
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

    public void setX(double val, int deriv) {
        x[deriv] = val;
    }

    public void setY(double val, int deriv) {
        y[deriv] = val;
    }

    public void setActive(boolean set) {
        active = set;
    }

    public boolean isActive() {
        return active;
    }

    public double getBatt() {
        return batt;
    }

    public void setBatt(double newBatt) {
        batt = newBatt;
    }

    public double getMaxBatt() {
        return hull.getBatt();
    }

    public double getHP() {
        return hp;
    }

    public void setHP(double newHp) {
        hp = newHp;
    }

    public double getMaxHP() {
        return hull.getHP();
    }

    public boolean isShortlived() {
        return shortlived;
    }
}
