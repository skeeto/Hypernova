package hypernova;

import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.lang.Math;

public class Loot extends Mass {
    public static final double MAG_MIN = 15;
    public static final double MAG_RATE = 8;
    public static final int LOOT_TTL = 5000;
    public static final double SPIN_RATE = 0.1;
    public static final double DRIFT_RATE = 0.5;
    private static final Random RNG = new Random();
    private static double magnetRange = 50;

    private Mass cargo;
    private long gold;

    public Loot(Mass src, Mass cargo, long gold) {
        super("loot");
        shortlived = true;
        suffersdrag = true;
        ttl = LOOT_TTL;
        if (src != null) {
            setPosition(src);
            x[1] = src.x[1];
            y[1] = src.y[1];
        }
        x[1] += RNG.nextGaussian() * DRIFT_RATE;
        y[1] += RNG.nextGaussian() * DRIFT_RATE;
        a[1] = RNG.nextGaussian() * SPIN_RATE;
        this.cargo = cargo;
        this.gold = gold;
    }

    public static void setLootMagnetRange(double range) { magnetRange = range; }

    @Override
    public void step(double t) {
        x[2] = 0;
        y[2] = 0;
        super.step(t);
        Rectangle2D bounds = getHit().getBounds2D();
        Ship player = Universe.get().getPlayer();
          
        // Magnetic money
        Rectangle2D magnet = new Rectangle2D.Double( x[0] - magnetRange, y[0] - magnetRange
                                                   , magnetRange * 2, magnetRange * 2);
        double px = player.getX(0);
        double py = player.getY(0); 
        short doCashout = 0;//false;
        if(magnet.contains(px,py))
        {
           double dx = px - x[0];
           double dy = py - y[0];
           if( dx > MAG_MIN || dx < -MAG_MIN ) x[0] += dx/MAG_RATE;
           else doCashout ++;
           if( dy > MAG_MIN || dy < -MAG_MIN ) y[0] += dy/MAG_RATE;
           else doCashout ++;
        }

        if ( doCashout >= 2 ) { //player.getHit().intersects(bounds)) {
            x[1] = player.getX(1);
            y[1] = player.getY(1);
            cashout(player);
        } 
            
    }

    @Override
    public double getMass() {
        double mass = 0;
        if (cargo != null)
            mass = cargo.getMass();
        return super.getMass() + mass;
    }

    private void cashout(Ship s) {
        Universe u = Universe.get();
        if (cargo != null) {
            s.store(cargo);
            u.queueMessage("Picked up a " + cargo);
        } else {
            if (gold > 0) {
                u.changeGold(gold);
                u.queueMessage("Picked up " + gold + " gold.");
            } else {
                u.queueMessage("Nothing valuable here");
            }
        }
        destruct();
    }
}
