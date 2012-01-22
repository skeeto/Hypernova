package hypernova;

import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.lang.Class;

import hypernova.universes.*;
import hypernova.gui.Transition;

public class SaveGame extends Thread implements Serializable
{
   private long totalPlayTime = 0;
   private double restoreX = 0;
   private double restoreY = 0;   
   private UniNames restoreU = UniNames.START;
   private static int curSlot = 0;
   private static Universe u = Universe.get();

   protected static SaveGame INSTANCE = new SaveGame();
   public static void autosave() { save(0); }

   static final long serialVersionUID = 1027533472837495L;  

   public static void checkpoint(){ load(0); }
   public static void setCheckpoint(double x, double y, UniNames u)
   {
     INSTANCE.restoreX = x;
     INSTANCE.restoreY = y;
     INSTANCE.restoreU = u;
     autosave();
   }
   
   public static UniNames getUniName() 
   {
      return INSTANCE.restoreU;
   }

   public static void writeFile(SaveGame obj)
   {
      String filename = obj.getClass().getName() + ".dat";
      FileOutputStream fos = null;
      ObjectOutputStream out = null;
      try
      {
        fos = new FileOutputStream("saves/" + curSlot + "/" + filename);
        out = new ObjectOutputStream(fos);
        out.writeObject(obj);
        out.close();
      } catch(IOException ex) {
        ex.printStackTrace();
      }
   }

   public static SaveGame loadFile(SaveGame obj)
   {
      SaveGame ret = null;
      String filename = obj.getClass().getName() + ".dat";
      FileInputStream fis = null;
      ObjectInputStream in = null;
      try
      {
         fis = new FileInputStream("saves/" + curSlot + "/" + filename);
         in = new ObjectInputStream(fis);
         ret = (SaveGame)in.readObject();
         in.close();
      } catch(Exception ex) {
         ex.printStackTrace();
      }
      return ret;
   }

   public static void load(int slot)
   {
     curSlot = slot;
     SaveGame.INSTANCE = loadFile(SaveGame.INSTANCE);
     Test.INSTANCE = (Test)loadFile(Test.INSTANCE);
     Start.INSTANCE = (Start)loadFile(Start.INSTANCE);
     Transition.startTransition(Transition.Types.FADE);


     NewUniverse nu = null;
     switch(INSTANCE.restoreU)
     {
        case TEST:
          nu = new Test();
          break;
        case START:
          nu = new Start();
          break;
     }     
     Ship p = u.getPlayer();   
     u.loadUniverse(nu);
     p.setX(INSTANCE.restoreX,0);
     p.setY(INSTANCE.restoreY,0);
     p.setA(Math.PI/2,0);
     p.setX(0,1);
     p.setY(0,1);
     p.setA(0,1);
     p.setX(0,2);
     p.setY(0,2);
     p.setA(0,2);
     p.setHP(p.getMaxHP());
   }

   public static void save(int slot)
   { 
     if(slot == 0) u.queueCornerMessage("Autosaving...");
     else u.queueCornerMessage("Saved in slot: " + slot);
     curSlot = slot; 
     (new SaveGame()).start(); 
   }
     
   public void run() {
     writeFile(SaveGame.INSTANCE);
     writeFile(Start.INSTANCE);
     writeFile(Test.INSTANCE);
   }

}
