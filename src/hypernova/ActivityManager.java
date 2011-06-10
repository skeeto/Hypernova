package hypernova;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.lang.ClassNotFoundException;
import org.codehaus.groovy.control.CompilationFailedException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.lang.ClassCastException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import groovy.lang.GroovyClassLoader;
import java.io.IOException;
import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class ActivityManager {

    private static Logger log = Logger.getLogger("ActivityManager");
    private static GroovyClassLoader gcl = new GroovyClassLoader();

    public ActivityManager() {
	log.setLevel(Level.INFO);
    }

    public Activity loadActivity(String name) {
	String fullPath = "hypernova.activities." + name;

	try {
	    Class clazz = gcl.loadClass(fullPath, true, false);
	    Activity act = (Activity)clazz.newInstance();
	    act.setUniverse(Universe.get());
	    return act;
	} catch (ClassNotFoundException ex) {
	    log.error("Couldn't find class " + fullPath);
	    return null;
	} catch (CompilationFailedException ex) {
	    log.error("Failed to compile class " + fullPath);
	    return null;
	} catch (InstantiationException ex) {
	    log.error("Couldn't instantiate " + fullPath);
	    return null;
	} catch (IllegalAccessException ex) {
	    log.error("Illegal access within " + fullPath);
	    return null;
	} catch (ClassCastException ex) {
	    log.error("Couldn't cast the loaded class to an Activity " + fullPath);
	    return null;
	}
    }

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
	log.setLevel(Level.INFO);

	System.out.println("Hello world");
	if(args.length != 1) {
	    System.out.println("usage: java hypernova.Activty [script]");
	    System.exit(1);
	}

	String tgt_name = args[0];

	ActivityManager at = new ActivityManager();
	Activity act = at.loadActivity(tgt_name);
	act.initialize();
    }
}
