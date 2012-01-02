package hypernova.audio;

import java.util.ArrayList; 
import java.io.*;
import java.util.Collections;

import hypernova.Universe;

public class SongPlaylist extends MinimWrapper //TODO extend some interface to get a callback to nextSong
{
   private static ArrayList<String> toPlay  = new ArrayList<String>();
   private static ArrayList<String> didPlay = new ArrayList<String>();
   private static String curSong = "";
   private static boolean shuffle = false;
   private static boolean showSong = true;
   

   public static void showSong(boolean v)
   { 
     showSong = v;
   }

   public static boolean showSong()
   { 
     return showSong;
   }

// TODO Super call in construct


   private static String addPlaylist(String file, boolean isPls)
   {
       String ret = ""; 

       try
       {
         FileInputStream fstream = new FileInputStream(file);
         DataInputStream in = new DataInputStream(fstream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         String line;
         while ((line = br.readLine()) != null)   
         {
            String linep = line.trim();
            if( isPls )
            {
                if((linep.toUpperCase()).startsWith("FILE") && linep.indexOf("=") > 0)
                {
                   String tmpRet = addFile((linep.split("="))[1]);
                   if(tmpRet.startsWith("ERROR"))
                   {
                      if("".equals(ret)) ret = tmpRet;
                      else ret = "\n" + tmpRet;
                   }
                }
            } else { 
                if(!linep.startsWith("#"))
                {
                   String tmpRet = addFile(linep);
                   if(tmpRet.startsWith("ERROR"))
                   {
                      if("".equals(ret)) ret = tmpRet;
                      else ret = "\n" + tmpRet;
                   }
                }
            }
         }
         in.close();
       } catch (Exception e) { 
         ret += "ERROR: Someone set us up the bomb";
       }

       if("".equals(ret)) ret = "Playlist loaded successfully";
       return ret;
   }
 
   /** 
    * Add file to playback (mp3, pls, m3u)
    * @param file The location of the file to add
    */
   public static String addFile(String file)
   {
      String ret = "File loaded successfully";
      
      if (file.length()<3) ret = "ERROR: Filename too short";
      else if(!(new File(file)).isFile()) ret = "ERROR: File(" + file + ") does not exist";
      else {
        String fileEnd = (file.substring(file.length() - 3)).toUpperCase();
        if      ( "MP3".equals(fileEnd) ) toPlay.add(file);
        else if ( "PLS".equals(fileEnd) ) ret = addPlaylist(file, true);
        else if ( "M3U".equals(fileEnd) ) ret = addPlaylist(file, false);
        else ret = "ERROR: Invalid file extension(" + fileEnd + "), must be either mp3 or pls";
        if(shuffle) Collections.shuffle(toPlay);
      }
   
      return ret;
   }
   
   /** 
    * Turn playlist shuffling on or off
    * @param doShuffle true to turn on, false otherwise
    */
   public static void setShuffle(boolean doShuffle) 
   { 
      shuffle = doShuffle; 
      if(shuffle) 
      { 
         Collections.shuffle(toPlay);
         Collections.shuffle(didPlay);
      }
   }
   
   public static boolean getShuffle(){ return shuffle; }

   /**
    * Play the previous song
    * Does nothing if at beginning
    */
   public static void backwardSong()
   {
     if(didPlay.isEmpty()) return;
     toPlay.add(0,curSong);
     curSong = didPlay.remove(didPlay.size() - 1);
     instance.loadSong(curSong);
     instance.nextSong();
   }
     
   /**
    * Start playback, or goto the next song
    * Will restart playing all files if at end of playlist
    * If at the end and shuffle is on, re-shuffles the playlist
    */ 
   public static void forwardSong()
   {
      int i = 0;
      
      if("".equals(curSong)) { if(toPlay.isEmpty()) return; }
      else didPlay.add(curSong);
      
      if(toPlay.isEmpty())
      {
         ArrayList<String> toPlayOld = toPlay;
         toPlay  = didPlay;
         didPlay = toPlayOld;
         didPlay.clear();
         if(shuffle) Collections.shuffle(toPlay);
      }
      curSong = toPlay.remove(i);
      instance.loadSong(curSong);
      instance.nextSong();
      if(showSong) Universe.get().queueCornerMessage(curSong);  
   }


   public static void debug()
   {
      for(int i = 0; i < didPlay.size(); i ++) System.out.println(didPlay.get(i));  
      System.out.println("*" + curSong + "*");
      for(int i = 0; i < toPlay.size(); i ++) System.out.println(toPlay.get(i));  
      System.out.println("-------------------------");
   }

   class DebugThread extends Thread {
    public void run() {
      while(true) {
         MinimWrapper.loop();
         try{Thread.sleep(40);}
         catch(Exception e){e.printStackTrace();}
      }
    }
   }
   public void dbg() { (new DebugThread()).start(); }
   
   public static void main(String[] args)
   {
      MinimWrapper.init();
      System.out.println( "Commands:\n  > -- Next song\n  < -- Previous song\n  p -- Print playlist"
                        + "  s -- Toggle shuffle\n  q -- quit\n  l <FILE> -- load a file\n\n");
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String command = "";
      char c = ' ';
      String status = "";
      boolean playing = false; 
      while( c != 'q' )
      {
           System.out.print("# ");
           try 
           {
             command = br.readLine();
             c = command.charAt(0);
             switch(c)
             {
                case 'q':
                   status = "Quitting...";
                   break;
                case 'p':
                   debug();
                   break;
                case 'l':
                   status = addFile(command.split(" ")[1]);
                   break;
                case '>':
                   status = "Next song";
                   forwardSong();
                   if(!playing) (new SongPlaylist()).dbg();
                   playing = true;
                   break;
                case '<':
                   status = "Previous song";
                   backwardSong();
                   break;
                case 's':
                   boolean newShuffle = !getShuffle();
                   setShuffle(newShuffle);
                   if( newShuffle ) status = "Shuffle is on";
                   else status = "Shuffle is off";
                   break;
                default:
                   status = "Invalid Command";
             }
             System.out.println(status);
           } catch (Exception e) {}
      }
   }
}
