package hypernova.gui;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class Model {
    private List<Polygon> polygons = new ArrayList<Polygon>();
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
                        x[i] = result[i * 2];
                        y[i] = result[i * 2 + 1];
                    }
                    model.polygons.add(new Polygon(x, y));
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

    public double getSize() {
        return size;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public void scale(double s) {
        size *= s;
        for (Polygon p : polygons) {
            for (int i = 0; i < p.xs.length; i++) {
                p.xs[i] *= s;
                p.ys[i] *= s;
            }
        }
    }

    public Model copy() {
        Model copy = new Model();
        for (Polygon p : polygons) {
            copy.polygons.add(p.copy());
        }
        copy.size = size;
        return copy;
    }
}
