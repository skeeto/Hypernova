package hypernova;

import junit.framework.TestCase;

public class ShipTest extends TestCase {
    public static final double X = 10.4;
    public static final double Y = -3.4;

    private Ship ship = new Ship(X, Y, Math.PI);

    public void testGetXY() {
        assertEquals(X, ship.getX(), 1e-10);
        assertEquals(Y, ship.getY(), 1e-10);
        assertEquals(Math.PI, ship.getAz(), 1e-10);
    }
}
