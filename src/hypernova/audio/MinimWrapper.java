package hypernova.audio;

import processing.core.PApplet;
import ddf.minim.*;
import ddf.minim.analysis.*;
import java.lang.Math;

import hypernova.sounds.*;

/**
 * An audio wrapper for minim with the following features
 *  - Provides real-time FFT averages
 *  - Performs crossfading between successive tracks
 *  - Plays sound effects
 * @author  plurSKI
 */
public class MinimWrapper
{
   protected static MinimWrapper instance = null;
   
   private PApplet empty;
   private Minim minim;
   private FFT fftCalc;
   private AudioPlayer song0 = null;
   private AudioPlayer song1 = null;
   private AudioPlayer curSong = null;
   
   private static final int FADE_START = 8000;
   private boolean crossFade = true;
   private boolean doFade = false;
   private int fadeStop;
   private AudioPlayer fadeOut;
   private AudioPlayer fadeIn;
 
   private float   fft_total;
   private float[] fft_4   = new float[4];
   private float[] fft_8   = new float[8];
   private float[] fft_16  = new float[16];
   private float[] fft_32  = new float[32];
   private float[] maxSeen = new float[4];

   private int songCount = 0;
   private AudioEffect[] effects = new AudioEffect[5];
   private int effectCount = 0;

   private class Embedded extends PApplet 
   {
      private static final long serialVersionUID = 8526472195634776147L;
      public void setup() {noLoop();}
      public void draw() {}
   }

   protected MinimWrapper() 
   {
      empty = new Embedded();     
      empty.init();
      minim = new Minim(empty);
   }

   /**
    * Must be called before using MinimWrapper
    */
   public static void init(){if(instance == null) instance = new MinimWrapper();}
   
   /**
    * Returns how many songs have been fully ran, good for knowing when to load the next song
    */
   public static int getCount(){return instance.songCount;}
 
   /**
    * Time remaining for current song
    * @return Number of milliseconds left
    */
   public static int timeleft(){return instance.curSong.length() - instance.curSong.position();} 

   /**
    * Toggle whether to crossfade between songs
    * @param doCrossFade true to fade, false to instantly change
    */
   public static void setCrossfade(boolean doCrossfade) {instance.crossFade = doCrossfade;}
   
  /**
   * Loads a song to play, if one is already loaded it is queued as the next song to play
   * @param location The file location of the mp3 file to play
   */
   public static void loadSong(String location) 
   {
     AudioPlayer tmp = instance.minim.loadFile(location, 2048);
     if((instance.curSong != null && instance.curSong == instance.song1) || instance.song0 == null) instance.song0 = tmp;
     else instance.song1 = tmp;
   }



   public static void playSoundAsync(String file)
   {
      MinimThread p = new MinimThread(file);
      p.start();
    
   }
 
  /**
   * Play a code defined sound
   */
   public static void playSound(AudioCode s)
   {
     AudioOutput out = instance.minim.getLineOut(Minim.STEREO, 2048);
     s.setOut(out);
     out.addSignal(s);
   }

  /** 
   * Add an effect to a song
   * Only up to 5 total
   * return Integer for removeEffect
   */
   public static int addEffect(AudioEffect e)
   {
     if(instance.effectCount == 5) instance.effectCount = 0;
     removeEffect(instance.effectCount);
     instance.effects[instance.effectCount] = e;
     instance.curSong.addEffect(e);
     return instance.effectCount ++;
   }

  /** 
   * Remove an effect from a song
   */
   public static void removeEffect(int x)
   {
     AudioEffect e = instance.effects[x];
     if(e != null) instance.curSong.removeEffect(e);
     instance.effects[x] = null;
   }

  /**
   * Remove all effects
   */
   public static void removeAllEffects()
   {
     for(int i = 0; i < instance.effects.length; i ++) removeEffect(i);
   }

  /**
   * Play a sound once from a file location
   * Meant for sounds that only happen once (efficiency)
   */
   public static void playSound(String file)
   {
      (instance.minim.loadSample(file, 2048)).trigger();
   }

  /**
   * Play the next song, or start playing the first 
   */
   public static void nextSong()
   {
      if( (!instance.crossFade && instance.curSong != null) 
      ||  (instance.curSong == null && instance.song0 != null)
      ||  instance.doFade )
      {  
         if(instance.doFade)
         {
            instance.doFade = false;
            instance.fadeStop = 0;
            instance.fadeOut.pause();
         }
         if(instance.curSong == null) swapSong(instance.song0);
         else {
           instance.curSong.pause();
           SongPlaylist.forwardSong();
           if(instance.curSong == instance.song0) swapSong(instance.song1);
           else swapSong(instance.song0);
         }
         instance.fftCalc = new FFT(instance.curSong.bufferSize(), instance.curSong.sampleRate());
         instance.curSong.skip(5); // TODO: Is it really synced :P
         instance.curSong.play();
         for(int i = 0; i < 4; i ++) instance.maxSeen[0] = 0;
      } else instance.doFade = true;
    
   }
   
  /**
   * Maximum fft values seen for this song (4 bands)
   */
   public static float[] max()
   {
     return instance.maxSeen;
   }


  /**
   * Perform cleanup
   */
   public static void close() {/* TODO instance.minim.close(); */}

   private static void swapSong(AudioPlayer n)
   {
     removeAllEffects();
     instance.curSong = n;
     for(int i = 0; i < instance.effects.length; i ++)
       if(instance.effects[i] != null) 
         instance.curSong.addEffect(instance.effects[i]);
   }

  /**
   * Must be called in your programs main loop
   */
   public static void loop() {
      float x = 0;
      
      // Crossfader
      if(instance.crossFade && timeleft() < FADE_START && !instance.doFade) SongPlaylist.forwardSong();
      else if(instance.doFade)
      {
          if(instance.fadeStop== 0)
          {
             if(instance.curSong == instance.song0) instance.fadeIn = instance.song1;
             else instance.fadeIn = instance.song0;
             instance.fadeOut = instance.curSong;
             instance.fadeIn.setGain(-30);
             instance.fadeStop = instance.fadeOut.position() + 8000;
             instance.fadeIn.play();
             instance.doFade= true;
          } else {
             int t = (instance.fadeStop- instance.fadeOut.position()) / 400;
             instance.fadeIn.setGain(-t);
             instance.fadeOut.setGain(-20 + t);
             if(-t > (-20 + t)) swapSong(instance.fadeIn);
             if(t <= 0) 
             {
                 instance.fadeOut.pause();
                 instance.fadeStop = 0;
                 instance.doFade = false;
             }
          }
         
      } 
     
      // TODO Other FFT averages 8/16, make the calculations for this efficient (One loop)
      // FFT Averages
      float val32 = 0;
      instance.fft_total = 0;
      for(int i = 0; i < 32; i++, x = 0) 
      {
         for(int j = i*32; j < i*32 + 32; j ++) 
           x += Math.floor(instance.fftCalc.getBand(i) * 0.2);
         val32 = x / 32;
         instance.fft_32[i] = val32;
         instance.fft_total += val32;

         if( i%2 == 1 ) 
         {
           int index16 = (i - 1) / 2;
           instance.fft_16[index16] = (instance.fft_32[i - 1] + val32) / 2;
           if( i%4 == 3 ) 
           {
             int index8 = (i - 1) / 4;
             instance.fft_8[index8] = ( instance.fft_16[index16 - 1] 
                                      + instance.fft_16[index16]) / 2;
             if( i%8 == 7 )
             {
                instance.fft_4[(i - 1) / 8] = ( instance.fft_8[index8 - 1]
                                              + instance.fft_8[index8]) / 2;
             }
           }
         }
      }

      for(int i = 0; i < 4; i ++)
        instance.maxSeen[i] = Math.max(instance.maxSeen[i], instance.fft_4[i]);

      instance.fft_total /= 32;
      instance.fftCalc.forward(instance.curSong.mix);
   }

  /**
   * Get the FFT total average
   */
   public static float fft()
   {
     return instance.fft_total;
   }

  /**
   * Get the FFT averages
   * @param size Can be FFT_4, FFT_8, FFT_16, FFT_32
   */
   public static float[] fft(int x)
   {
     switch(x)
     {
       case 32: return instance.fft_32;
       case 16: return instance.fft_16;
       case 8: return instance.fft_8;
       case 4: return instance.fft_4;
     }
     return null;
   }

   public static void main (String[] args)
   {  
      MinimWrapper.init();
 //     MinimWrapper.setCrossfade(false);
      MinimWrapper.loadSong("test.mp3");
      MinimWrapper.nextSong();
      MinimWrapper.loadSong("test2.mp3");
      int count = 0;
      int sCount = MinimWrapper.getCount();
      while(true) {
         if(count++ == 100) MinimWrapper.nextSong();
         MinimWrapper.loop();
         if(MinimWrapper.getCount() > sCount) 
         {  
           sCount = MinimWrapper.getCount();
           MinimWrapper.loadSong("test3.mp3");
         }
         for(int i = 0; i < 100; i ++) System.out.println("\n\n\n\n\n\n\n");
         for(int i = 0; i < 4; i ++)
         {
            float x = (instance.fft(4))[i];
            for(int j = 0; j < x  && j < 100; j ++) System.out.print("-");
            System.out.println("|");
         }
         System.out.println("\n\n\n\n");
         for(int i = 0; i < 32; i ++) 
         {
            float x = (instance.fft(32))[i];
            for(int j = 0; j < x  && j < 100; j ++) System.out.print("*");
            System.out.println("|");
         }
         try{Thread.sleep(40);}
         catch(Exception e){e.printStackTrace();}
      }
     // System.exit(0);
   }
}
