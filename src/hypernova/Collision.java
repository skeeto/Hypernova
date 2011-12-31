package hypernova;

import hypernova.gui.Model;

/* Helper functions for collisions */
public class Collision {
   public static void hit(Mass m, Mass src, double damage, boolean destroy) {
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
  
    public static void bounce(Mass src) {
        // TODO: Make this more robust    
        src.setY(-src.getY(1),1);
        src.setX(-src.getX(1),1);
    }
}
