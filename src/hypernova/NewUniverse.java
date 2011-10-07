package hypernova;

import hypernova.Universe;

public class NewUniverse extends SaveGame {
  public static final double WIDTH  = 5000; 
  public static final double HEIGHT = 5000;
  protected static Universe u = Universe.get();
  static final long serialVersionUID = 3137533472837495L;  

  public void begin(){}
}
