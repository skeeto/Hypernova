package hypernova;

import junit.framework.TestCase;

public class MassTest extends TestCase {
    public static final double X = 10.4;
    public static final double Y = -3.4;

    private Mass mass = new Mass(null, X, Y, Math.PI);

    public void testGetXY() {
        assertEquals(X, mass.getX(), 1e-10);
        assertEquals(Y, mass.getY(), 1e-10);
        assertEquals(Math.PI, mass.getA(), 1e-10);
    }
}
