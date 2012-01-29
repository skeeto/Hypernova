package hypernova;

import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.ObjectOutputStream;
import java.awt.image.BufferedImage;
import java.io.ObjectInputStream;
import java.lang.Class;

import hypernova.universes.*;
import hypernova.gui.Transition;

public class SaveGame extends Thread implements Serializable
{
   private static BufferedImage screenshot = null;
   private long totalPlayTime = 0;
   private double restoreX = 0;
   private double restoreY = 0;   
   private UniNames restoreU = UniNames.START;
   private static int curSlot = 0;
   private static Universe u = Universe.get();

   protected static SaveGame INSTANCE = new SaveGame();
   private GameStats stats = new GameStats();
   private class GameStats implements Serializable {
     static final long serialVersionUID = 1027472837L;  
     public long playTime;
     public String uni;
     public int percent;
   }

   static final long serialVersionUID = 1027533472837495L;  

   public static void autosave() { save(0, null);}
   public static void checkpoint(){ load(0); }
   public static void setCheckpoint(double x, double y, UniNames u)
   {
     INSTANCE.restoreX = x;
     INSTANCE.restoreY = y;
     INSTANCE.restoreU = u;
     autosave();
   }

   public static boolean hasAutoSave()
   {
     File file=new File("saves/0/hypernova.SaveGame.dat");
     return file.exists();
   }
  
   public static UniNames getUniName() 
   {
      return INSTANCE.restoreU;
   }

   public static void writeFile(Object obj)
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

   public static Object loadFile(Object obj)
   {
      Object ret = null;
      String filename = obj.getClass().getName() + ".dat";
      FileInputStream fis = null;
      ObjectInputStream in = null;
      try
      {
         fis = new FileInputStream("saves/" + curSlot + "/" + filename);
         in = new ObjectInputStream(fis);
         ret = in.readObject();
         in.close();
      } catch(java.io.FileNotFoundException ex) {
         /* Do Nothing */
      } catch(Exception ex) {
         ex.printStackTrace();
      }
      return ret;
   }

   public static void load(int slot)
   {
     curSlot = slot;
     SaveGame.INSTANCE = (SaveGame)loadFile(SaveGame.INSTANCE);
     Test.INSTANCE = (Test)loadFile(Test.INSTANCE);
     Start.INSTANCE = (Start)loadFile(Start.INSTANCE);
     Alter.INSTANCE = (Alter)loadFile(Alter.INSTANCE);
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
        case ALTER:
          nu = new Alter();
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

   public static String saveStats(int slot)
   {
     String ret = "(" + slot + ") Empty";
     int prevSlot = curSlot;
     curSlot = slot;
     GameStats s = (GameStats)loadFile(SaveGame.INSTANCE.stats);
     curSlot = prevSlot;
     
     // TODO: Calculate a time
     if( s != null ) ret = "(" + slot + ") 11.5 hrs : " + s.uni 
                         + " -- " + s.percent + "%";
     return ret;
   }

   public static void save(int slot, BufferedImage img)
   { 
     SaveGame.INSTANCE.screenshot = img;
     if(slot == 0) u.queueCornerMessage("Autosaving...");
     else u.queueCornerMessage("Saved in slot: " + slot);
     curSlot = slot; 
     (new SaveGame()).start(); 
   }
     
   public void run() {
     GameStats s = SaveGame.INSTANCE.stats;
     s.uni = (SaveGame.INSTANCE.restoreU).toString();
      
     // TODO: Debug values
     s.playTime = 100;
     s.percent = 10;

     writeFile(SaveGame.INSTANCE.stats);
     writeFile(SaveGame.INSTANCE);
     writeFile(Start.INSTANCE);
     writeFile(Test.INSTANCE);
     writeFile(Alter.INSTANCE);
     try {
       File f = new File("saves/" + curSlot + "/screenshot.png");
       if(screenshot != null) ImageIO.write(screenshot, "PNG", f);
     } catch(Exception e) { e.printStackTrace(); }
   }

}
