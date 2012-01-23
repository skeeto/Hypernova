package hypernova.activities;

import hypernova.ActivitySimple;
import hypernova.gui.Movie;

public class MovieEvent extends ActivitySimple {
    public void begin(double px, double py) {
      Movie.begin("testMovie");
      this.finish();
    }

}
