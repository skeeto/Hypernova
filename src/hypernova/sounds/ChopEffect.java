package hypernova.sounds;

import ddf.minim.AudioEffect;

public class ChopEffect implements AudioEffect
{
  public void process(float[] samp)
  {
    for (int i = 0; i < samp.length; i++) samp[i] = samp[i % (samp.length / 3)];
  }
  
  public void process(float[] left, float[] right)
  {
    process(left);
    process(right);
  }
}
  
