package hypernova;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;

import java.util.Properties;

import groovy.lang.GroovyShell;
import groovy.lang.Binding;

import org.apache.log4j.Logger;

public class Activity {
    private static Logger log = Logger.getLogger("Activity");

    private String script;

    public static Activity get(String name) {
	Properties prop = new Properties();
	String fname = "activities/" + name + ".act";
        log.debug("Loading activity " + name + " (" + fname + ")");
	try {
	    prop.load(Activity.class.getResourceAsStream(fname));
	} catch (IOException ex) {
	    log.error("Failed to load activity " + name);
	    return null;
	}
	return new Activity(prop.getProperty("script"));
    }

    private Activity(String script) {
	this.script = script;
    }

    private Reader readerForResource(String resource) {
	InputStream is = Activity.class.getResourceAsStream(resource);
	if (is == null) {
	    log.error("Failed to load script " + resource);
	    throw new RuntimeException("error loading " + resource);
	}
	return new InputStreamReader(is);
    }

    public void realize(Universe universe, double realizationX, double realizationY) {
	Binding binding = new Binding();
	binding.setVariable("_universe_", universe);
	binding.setVariable("sceneX", realizationX);
	binding.setVariable("sceneY", realizationY);

	GroovyShell shell = new GroovyShell(binding);
	shell.evaluate("import hypernova.API; API.setUniverse(_universe_)");
	Reader reader = readerForResource(script);
	shell.evaluate(reader);
    }
}
