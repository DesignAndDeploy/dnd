package edu.teco.dnd.module;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A ClassLoader that supports injecting byte code (for example if classes are loaded via a network connection). The
 * injected classes will only be used if the parent ClassLoader cannot resolve the class.
 */
public class ApplicationClassLoader extends ClassLoader {
	private static final Logger LOGGER = LogManager.getLogger(ApplicationClassLoader.class);

	/**
	 * Byte code that was injected but has not been loaded as an actual class.
	 */
	private final Map<String, byte[]> unloadedClasses = new HashMap<String, byte[]>();

	/**
	 * Injects a class into this ClassLoader. This class can then be resolved with this ClassLoader. However, the byte
	 * code is only used if the parent ClassLoader cannot resolve the class.
	 * 
	 * @param name
	 *            the name of the class
	 * @param byteCode
	 *            the byte code of the class
	 */
	public void injectClass(final String name, final byte[] byteCode) {
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

	@Override
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
