package edu.teco.dnd.module;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.ConnectionManager;

/**
 * Classloader used by Applications. Capable of loading classes whose bytecode has been handed in by the appropriate
 * method beforehand. Might also do additional security checks to enforce some constraints.
 * 
 * @author Marvin Marx
 * 
 */
public class ApplicationClassLoader extends ClassLoader {

	private static final Logger LOGGER = LogManager.getLogger(ApplicationClassLoader.class);

	private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

	/**
	 * Instantiate a new classloader. Must obviously use the same instance for loading bytecode and then loading
	 * classes.
	 * 
	 * @param connMan
	 *            the connection Manager. Used for optional on demand class bytecode loading.
	 * @param associatedAppId
	 *            the Application associated with this classloader. Used for the same reason. (Note that this
	 *            capabilities are currently not implemented.)
	 */
	public ApplicationClassLoader(ConnectionManager connMan, UUID associatedAppId) {
	}

	/** empty constructor, disable on demand class loading. */
	public ApplicationClassLoader() {
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

	/**
	 * Load bytecode of a class into classloader so as to be able to load this exact class when a class of $name is
	 * requested.
	 * 
	 * @param name
	 *            when the class of name $name is requested, this bytecode will be loaded. Name must be unique. Else
	 *            only the bytecode of the first invocation will be used.
	 * @param classData
	 *            The bytecode of the class to load.
	 */
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
