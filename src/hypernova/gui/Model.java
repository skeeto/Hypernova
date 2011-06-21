package hypernova.gui;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.PathIterator;

import org.apache.log4j.Logger;

import hypernova.Mass;

public class Model {
    public static final double BREAKUP_DIVISION = 0.05;

    private Shape shapes;
    private Shape filled;
    private AffineTransform at = new AffineTransform();
    private double size = 1.0;

    private static Map<String, Model> cache = new HashMap<String, Model>();
    private static Logger log = Logger.getLogger("gui.Model");

    protected Model(Shape s, double size) {
        shapes = s;
        filled = new Path2D.Double();
        this.size = size;
    }

    protected Model() {
    }

    public static synchronized Model get(String name) {
        Model model = cache.get(name);
        if (model != null) return model.copy();
        model = new Model();
        String filename = "models/" + name + ".mdl";
        log.debug("Loading model '" + name + "' (" + filename + ")");
        Path2D.Double solid = new Path2D.Double();
        Path2D.Double line = new Path2D.Double();
        try {
            InputStream s = Model.class.getResourceAsStream(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(s));
            while (true) {
                String str = in.readLine();
                if (str == null || str.length() < 5) break;
                if ("path".equals(str.substring(0, 4))) {
                    line.append(path(readList(str.substring(4), 1)), false);
                } else if ("fpath".equals(str.substring(0, 5))) {
                    solid.append(path(readList(str.substring(5), 1)), false);
                } else if ("oval".equals(str.substring(0, 4))) {
                    line.append(oval(readList(str.substring(4), 1)), false);
                } else if ("foval".equals(str.substring(0, 5))) {
                    solid.append(oval(readList(str.substring(5), 1)), false);
                }
            }
            model.shapes = line;
            model.filled = solid;
            cache.put(name, model);
        } catch (java.io.IOException e) {
            log.error("Failed to load model '" + name + "' " + e.getMessage());
            /* TODO handle more gracefully. */
            return null;
        }
        return model.copy();
    }

    public static double[] readList(String str, int skip) {
        String[] s = str.split("\\s+");
        double[] ns = new double[s.length - skip];
        for (int i = skip; i < s.length; i++) {
            ns[i-skip] = Double.parseDouble(s[i]);
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

    public Shape getShape() {
        return at.createTransformedShape(shapes);
    }

    public Shape getFilled() {
        return at.createTransformedShape(filled);
    }

    public void transform(double x, double y, double rotate) {
        at.setToTranslation(x, y);
        at.scale(size, size);
        at.rotate(rotate);
    }

    public void transform(Mass src) {
        transform(src.getX(0), src.getY(0), src.getA(0));
    }

    public Model copy() {
        Model copy = new Model();
        copy.shapes = shapes;
        copy.filled = filled;
        copy.size = size;
        return copy;
    }

    public Model[] breakup() {
        List<Model> models = new ArrayList<Model>();
        double[] coords = new double[6];
        double[] last = new double[2];
        PathIterator i = shapes.getPathIterator(null, BREAKUP_DIVISION);
        while (!i.isDone()) {
            int type = i.currentSegment(coords);
            switch (type) {
            case PathIterator.SEG_LINETO:
                Line2D.Double l = new Line2D.Double(last[0], last[1],
                                                    coords[0], coords[1]);
                models.add(new Model(l, size));
                break;
            case PathIterator.SEG_QUADTO:
                QuadCurve2D.Double q
                = new QuadCurve2D.Double(last[0], last[1],
                                         coords[0], coords[1],
                                         coords[2], coords[3]);
                models.add(new Model(q, size));
                break;
            case PathIterator.SEG_CUBICTO:
                CubicCurve2D.Double c =
                    new CubicCurve2D.Double(last[0], last[1],
                                            coords[0], coords[1],
                                            coords[2], coords[3],
                                            coords[4], coords[5]);
                models.add(new Model(c, size));
                break;
            }
            last[0] = coords[0];
            last[1] = coords[1];
            i.next();
        }
        return models.toArray(new Model[0]);
    }
}
