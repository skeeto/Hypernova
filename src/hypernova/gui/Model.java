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
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;

public class Model {
    private Shape[] shapes;
    private Shape[] transformed;
    private boolean[] filled;
    private AffineTransform at = new AffineTransform();
    private double size = 10.0;

    private static Map<String, Model> cache = new HashMap<String, Model>();
    private static Logger log = Logger.getLogger("gui.Model");

    protected Model() {
    }

    public static synchronized Model getModel(String name) {
        Model model = cache.get(name);
        if (model != null) return model.copy();
        model = new Model();
        List<Shape> shapes = new ArrayList<Shape>();
        List<Boolean> filled = new ArrayList<Boolean>();
        String filename = "models/" + name + ".mdl";
        log.debug("Loading model '" + name + "' (" + filename + ")");
        try {
            InputStream s = Model.class.getResourceAsStream(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(s));
            while (true) {
                String str = in.readLine();
                if (str == null || str.length() < 5) break;
                if ("path".equals(str.substring(0, 4))) {
                    shapes.add(path(readList(str.substring(4))));
                    filled.add(false);
                } else if ("fpath".equals(str.substring(0, 5))) {
                    shapes.add(path(readList(str.substring(5))));
                    filled.add(true);
                } else if ("oval".equals(str.substring(0, 4))) {
                    shapes.add(oval(readList(str.substring(4))));
                    filled.add(false);
                } else if ("foval".equals(str.substring(0, 5))) {
                    shapes.add(oval(readList(str.substring(5))));
                    filled.add(true);
                }
            }
            model.shapes = shapes.toArray(new Shape[0]);
            model.transformed = new Shape[shapes.size()];
            model.filled = new boolean[shapes.size()];
            for (int i = 0; i < filled.size(); i++)
                model.filled[i] = filled.get(i);
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

    private static Shape path(double[] vals) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(vals[0], vals[1]);
        for (int i = 1; i < vals.length / 2; i++) {
            path.lineTo(vals[i * 2], vals[i * 2 + 1]);
        }
        return path;
    }

    private static Shape oval(double[] vals) {
        if (vals.length < 4 || vals.length > 5)
            throw new RuntimeException("Invalid oval");
        double w = vals[2] * 2;
        double h = vals[3] * 2;
        double x = vals[0] - w / 2;
        double y = vals[1] - h / 2;
        Shape oval = new Ellipse2D.Double(x, y, w, h);
        if (vals.length == 5) {
            AffineTransform at = new AffineTransform();
            at.rotate(Math.toRadians(vals[4]));
            oval = at.createTransformedShape(oval);
        }
        return oval;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double val) {
        size = val;
    }

    public Shape[] getShapes() {
        return transformed;
    }

    public boolean[] getFilled() {
        return filled;
    }

    public void transform(double x, double y, double rotate) {
        at.setToTranslation(x, y);
        at.scale(size, size);
        at.rotate(rotate);
        apply();
    }

    private void apply() {
        for (int i = 0; i < shapes.length; i++) {
            transformed[i] = at.createTransformedShape(shapes[i]);
        }
    }

    public Model copy() {
        Model copy = new Model();
        copy.shapes = shapes;
        copy.transformed = new Shape[shapes.length];
        copy.filled = filled;
        copy.size = size;
        copy.apply();
        return copy;
    }
}
