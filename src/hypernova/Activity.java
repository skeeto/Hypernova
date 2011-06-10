package hypernova;

public abstract class Activity {
    protected Universe universe;

    public abstract void initialize();


    protected void setUniverse(Universe universe) {
	this.universe = universe;
    }

    protected ShipBuilder makeShip(String kind) {
	Ship ship = new Ship(kind);
	return new ShipBuilder(ship);
    }

    class ShipBuilder {
	private String weapon = "blaster";
	private double px = 0;
	private double py = 0;
	private double theta = 0;
	private double size = 5.0;
	private Ship ship;

	ShipBuilder(Ship ship) {
	    this.ship = ship;
	}

	public ShipBuilder withWeapon(String name) {
	    weapon = name;
	    return this;
	}

	public ShipBuilder atPosition(double x, double y) {
	    px = x;
	    py = y;
	    return this;
	}

	public ShipBuilder withRotation(double theta) {
	    this.theta = theta;
	    return this;
	}

	public ShipBuilder withSize(double size) {
	    this.size = size;
	    return this;
	}

	public Ship add() {
	    ship.setWeapon(weapon, 0);
	    ship.setPosition(px, py, theta);
	    ship.setSize(size);
	    universe.add(ship);
	    return ship;
	}

	public Ship addAsPlayer() {
	    add();
	    universe.setPlayer(ship);
	    return ship;
	}
    }

}

