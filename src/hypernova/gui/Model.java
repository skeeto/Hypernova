package hypernova.gui;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;

public class Model {
    public static final double MODEL_SCALE = 10.0;

    private List<Shape> shapes = new ArrayList<Shape>();
    private List<Shape> transformed = new ArrayList<Shape>();
    private AffineTransform at = new AffineTransform();
    private double size = 1.0;

    private static Map<String, Model> cache = new HashMap<String, Model>();
    private static Logger log = Logger.getLogger("gui.Model");

    public static synchronized Model getModel(String name) {
        Model model = cache.get(name);
        if (model != null) return model.copy();
        model = new Model();
        String filename = "models/" + name + ".mdl";
        log.debug("Loading model '" + name + "' (" + filename + ")");
        try {
            InputStream s = Model.class.getResourceAsStream(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(s));
            while (true) {
                String str = in.readLine();
                if (str == null) break;
                if ("poly".equals(str.substring(0, 4))) {
                    double[] result = readList(str.substring(4));
                    double[] x = new double[result.length / 2];
                    double[] y = new double[result.length / 2];
                    for (int i = 0; i < x.length; i++) {
                        x[i] = result[i * 2] * MODEL_SCALE;
                        y[i] = result[i * 2 + 1] * MODEL_SCALE;
                    }
                    model.shapes.add(makePath(x, y));
                } else if ("oval".equals(str.substring(0, 4))) {
                    double[] result = readList(str.substring(4));
                }
            }
            cache.put(name, model);
        } catch (java.io.IOException e) {
            log.error("Failed to load model '" + name + "' " + e.getMessage());
            /* TODO handle more gracefully. */
            return null;
        }
        return model.copy();
    }

    private static double[] readList(String str) {
        String[] s = str.split("\\s+");
        double[] ns = new double[s.length - 1];
        for (int i = 1; i < s.length; i++) {
            ns[i-1] = Double.parseDouble(s[i]);
        }
        return ns;
    }

    private static Path2D.Double makePath(double[] x, double[] y) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(x[0], y[0]);
        for (int i = 1; i < x.length; i++) {
            path.lineTo(x[i], y[i]);
        }
        return path;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double val) {
        size = val;
    }

    public List<Shape> getShapes() {
        return transformed;
    }

    public void scale(double s) {
        at.setToScale(s * size, s * size);
        apply();
    }

    public void rotate(double a) {
        at.setToRotation(a);
        apply();
    }

    public void translate(double x, double y) {
        at.setToTranslation(x, y);
        apply();
    }

    public void transform(double x, double y, double rotate) {
        at.setToScale(size, size);
        at.translate(x, y);
        at.rotate(rotate);
        apply();
    }

    private void apply() {
        transformed.clear();
        for (Shape s : shapes) {
            transformed.add(at.createTransformedShape(s));
        }
    }

    public Model copy() {
        Model copy = new Model();
        copy.shapes = shapes;
        copy.size = size;
        copy.apply();
        return copy;
    }
}
