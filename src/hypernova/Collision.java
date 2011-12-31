package hypernova;

import hypernova.gui.Model;

/* Helper functions for collisions */
public class Collision {
    public static void hit(Mass m, Mass src, double damage, boolean destroy) {
        /* TODO: calculate damage
                 remove object from the universe if below 0. */
        m.damage(damage);
        if (Config.showDamage()) {
            Mass txt = new Mass(new Hull(new Model("" + damage)));
            txt.setPosition(src);
            txt.setFaction(src.getFaction());
            txt.shortlived = true;
            txt.ttl = 15;
            txt.setA(0, 0);
            txt.setY(-0.7, 1);
            txt.setSize(2.0);
            Universe.get().add(txt);
        }
        if(destroy) {
            Universe.get().remove(src); 
        }
    }
}
