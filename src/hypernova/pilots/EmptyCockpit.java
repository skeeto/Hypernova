package hypernova.pilots;

public class EmptyCockpit extends Pilot {
    public EmptyCockpit() {
        super(null);
    }

    public void drive() {
        /* We're asleep at the wheel here. */
    }
}
