package hypernova.gui;

import java.util.Arrays;

public class Polygon {
    public double[] xs;
    public double[] ys;

    /* Rotated vertices. */
    public double[] rxs;
    public double[] rys;

    public Polygon(double[] x, double[] y) {
        xs = x;
        ys = y;
        rxs = new double[x.length];
        rys = new double[y.length];
    }

    public void rotate(double a) {
        for (int i = 0; i < xs.length; i++) {
            rxs[i] = xs[i] * Math.cos(a) - ys[i] * Math.sin(a);
            rys[i] = xs[i] * Math.sin(a) + ys[i] * Math.cos(a);
        }
    }

    public Polygon copy() {
        Polygon copy = new Polygon(Arrays.copyOf(xs, xs.length),
                                   Arrays.copyOf(ys, ys.length));
        return copy;
    }
}
