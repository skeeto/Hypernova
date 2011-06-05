package hypernova;

public class Ship {
    private double x, y, az;

    public Ship(double x, double y, double azimuth) {
        this.x = x;
        this.y = y;
        this.az = azimuth;
    }

    public Ship(double x, double y) {
        this(x, y, 0.0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAz() {
        return az;
    }
}
