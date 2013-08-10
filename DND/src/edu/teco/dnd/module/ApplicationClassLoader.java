package edu.teco.dnd.module;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.ConnectionManager;

public class ApplicationClassLoader extends ClassLoader {

	private static final Logger LOGGER = LogManager.getLogger(ApplicationClassLoader.class);

	private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

	public ApplicationClassLoader(ConnectionManager connMan, UUID associatedAppId) {
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolveIt) throws ClassNotFoundException {

		Class<?> clazz = classes.get(name);
		if (clazz != null) {
			return clazz; // must always return same if requested twice!
		}

		try {
			return super.loadClass(name, resolveIt);
		} catch (ClassNotFoundException e) {
			// Continuing below. Error is expected behavior.
		}

		LOGGER.trace("app loading class: " + name);

		if (name.startsWith("java.")) {
			throw new ClassNotFoundException("a part of the fully classified name \"" + name
					+ "\" is not permitted by this classloader.");
		}
		System.getSecurityManager().checkPackageAccess(name);

		byte[] clBytes = classBytes.get(name);
		if (clBytes != null) {
			clazz = defineClass(name, clBytes, 0, clBytes.length);
			if (clazz == null) {
				throw new ClassFormatError();
			}

			if (resolveIt) {
				resolveClass(clazz);
			}

			classes.put(name, clazz);
			classBytes.remove(name);
			return clazz;
		} else {
			LOGGER.warn("class " + name + " bytecode was not loaded before it was instantiated.");
			throw new ClassNotFoundException();
			// TODO This would be a good point to request class bytecode from other modules, if so desired.
		}
	}

	public void appLoadClass(String name, byte[] classData) {
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}
		if (classData == null) {
			throw new IllegalArgumentException("classData must not be null");
		}

		if (!classes.containsKey(name) && !classBytes.containsKey(name)) {
			classBytes.put(name, classData);
		} else {
			LOGGER.debug("double loaded bytecode of class: " + name);
		}
	}

}

