package edu.teco.dnd.module;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classloader used by Applications. Capable of loading classes whose bytecode has been handed in by the appropriate
 * method beforehand. Might also do additional security checks to enforce some constraints.
 * 
 * @author Marvin Marx
 * 
 */
public class ApplicationClassLoader extends ClassLoader {
	private static final Logger LOGGER = LogManager.getLogger(ApplicationClassLoader.class);

	private final Map<String, byte[]> unloadedClasses = new HashMap<String, byte[]>();
	
	public void addClass(final String name, final byte[] byteCode) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("illegal name");
		}
		if (byteCode == null) {
			throw new IllegalArgumentException("byteCode must not be null");
		}
		
		synchronized (unloadedClasses) {
			if (!unloadedClasses.containsKey(name)) {
				unloadedClasses.put(name, byteCode);
			}
		}
	}

	// TODO: this probably does not work with inner classes
	public Class<?> findClass(final String name) {
		LOGGER.entry(name);
		byte[] byteCode = null;
		synchronized (unloadedClasses) {
			byteCode = unloadedClasses.get(name);
			if (byteCode != null) {
				unloadedClasses.put(name, null);
			}
		}
		if (byteCode == null) {
			return LOGGER.exit(null);
		}
		
		return LOGGER.exit(defineClass(name, byteCode, 0, byteCode.length));
	}
}
