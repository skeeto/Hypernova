package hypernova.audio;

import ddf.minim.*;

public abstract class AudioCode implements AudioSignal
{
  private AudioOutput out = null;

  public abstract void generate(float[] samp);
  
  public void setOut(AudioOutput out) { this.out = out;} 
  public void stop() { out.close(); }
  public void generate(float[] left, float[] right)
  {
    generate(left);
    generate(right);
  }

}
