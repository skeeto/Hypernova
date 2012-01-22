package hypernova.sounds;

import ddf.minim.AudioEffect;

public class VolumeEffect implements AudioEffect
{
  public static int amount = 0;
  public void process(float[] samp)
  {
    for (int i = 0; i < samp.length; i++) samp[i] = (samp[i]*(amount + 100)) / 1000 ;
  }
  
  public void process(float[] left, float[] right)
  {
    process(left);
    process(right);
  }
}
  
