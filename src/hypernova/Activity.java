package hypernova;

import java.lang.RuntimeException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;
import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import java.util.Properties;
import org.apache.log4j.Logger;

public class Activity {
    private static Logger log = Logger.getLogger("Activity");
    private static String defaultAPI = "activities/api.groovy";

    private String script;

    public static Activity get(String name) {
	Properties prop = new Properties();
	String fname = "activities/" + name;

	try {
	    prop.load(Activity.class.getResourceAsStream(fname));
	} catch (IOException ex) {
	    log.error("error loading " + fname);
	    return null;
	}

	String script = prop.getProperty("script");

	return new Activity(script);
    }

    private Activity(String script) {
	this.script = script;
    }

    private Reader readerForResource(String resource) {
	InputStream is = Activity.class.getResourceAsStream(resource);
	if(is == null) {
	    log.error("error loading " + resource);
	    throw new RuntimeException("error loading " + resource);
	}

	Reader reader = new InputStreamReader(is);
	return reader;
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

