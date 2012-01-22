package hypernova.sounds;

import ddf.minim.AudioEffect;

public class WarbleEffect implements AudioEffect
{
  public static boolean l = false;
  public static boolean r = false;

  public void process(float[] samp)
  {
    for (int i = 0; i < samp.length / 2; i++) samp[i] = samp[i] * samp[i];
  }
  
  public void process(float[] left, float[] right)
  {
    if(l) process(left);
    if(r) process(right);
  }
}
  
