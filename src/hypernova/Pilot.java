package hypernova;

public abstract class Pilot {
    protected final Ship ship;

    public Pilot(Ship ship) {
        this.ship = ship;
    }

    public abstract void drive();
}
