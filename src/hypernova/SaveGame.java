package hypernova;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class SaveGame implements Serializable
{
   private long totalPlayTime = 0;
   private double restoreX = 0;
   private double restoreY = 0;   
   private UniName restoreU = UniName.START;
   private static SaveGame sg = new SaveGame();
   
   static final long serialVersionUID = 1027533472837495L;  

   public enum UniName { TEST
                       , START
                       };

   public static void autoLoad(){ load(0); }
  
   public static void autoSave(double x, double y, UniName u)
   {
     sg.restoreX = x;
     sg.restoreY = y;
     sg.restoreU = u;
     save(0); 
   }

   public static void load(int slot)
   {
     try {
        FileInputStream fis = new FileInputStream("saves/" + slot + ".sav"); 
        GZIPInputStream gzis = new GZIPInputStream(fis); 
        ObjectInputStream in = new ObjectInputStream(gzis);  
        sg = (SaveGame)in.readObject();
        in.close();
     } catch (Exception e) {
        e.printStackTrace();
     }

     NewUniverse nu = null;
     switch(sg.restoreU)
     {
        case TEST:
          nu = new hypernova.universes.Test();
          break;
        case START:
          nu = new hypernova.universes.Start();
          break;
     }     
     Universe u = Universe.get();
     Ship p = u.getPlayer();   
     u.loadUniverse(nu);
     p.setX(sg.restoreX,0);
     p.setY(sg.restoreY,0);
     p.setA(0,0);
     p.setX(0,1);
     p.setY(0,1);
     p.setA(0,1);
     p.setX(0,2);
     p.setY(0,2);
     p.setA(0,2);
   }

   public static void save(int slot)
   {
     try
     {
        FileOutputStream fos = new FileOutputStream("saves/" + slot + ".sav");
        GZIPOutputStream gzos = new GZIPOutputStream(fos); 
        ObjectOutputStream out = new ObjectOutputStream(gzos);
        out.writeObject(sg);
        out.flush();
        out.close();
     } catch (Exception e) {
        e.printStackTrace();
     }
   }
}
