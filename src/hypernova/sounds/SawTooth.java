package hypernova.sounds;

import hypernova.audio.AudioCode;
public class SawTooth extends AudioCode
{
  private int count = 0;
  private int countp = 5;
  public void generate(float[] samp)
  {
    if(count++ > 30) { count = 1; countp --; }
    if(countp <= 0) stop();
    float inter = samp.length / (countp*15 + count);
    for ( int i = 0; i < samp.length; i += inter )
      for ( int j = 0; j < inter && (i+j) < samp.length; j++ )
        samp[i + j] = j;
  }
}
