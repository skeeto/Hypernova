package hypernova.pilots;

public class EmptyCockpit extends Pilot {
    public EmptyCockpit() {
        super(null);
    }

    @Override
    public void drive() {
        /* We're asleep at the wheel here. */
    }
}
