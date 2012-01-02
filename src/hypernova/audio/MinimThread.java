package hypernova.audio;

import processing.core.PApplet;
import ddf.minim.*;
import ddf.minim.analysis.*;


/**
 * An audio wrapper for minim with the following features
 *  - Provides real-time FFT averages
 *  - Performs crossfading between successive tracks
 *  - Plays sound effects
 * @author  plurSKI
 */
public class MinimThread extends Thread
{
         private String song = "";
         MinimThread (String file) {this.song = file;}
         public void run() { MinimWrapper.playSound(song);}

}
