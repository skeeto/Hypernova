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

    public void setAz(double val) {
        az = val;
    }

    public void addAz(double rate) {
        az += rate;
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
