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
	private ConnectionManager connMan;
	private UUID associatedAppId;

	public ApplicationClassLoader(ConnectionManager connMan, UUID associatedAppId) {
		this.connMan = connMan;
		this.associatedAppId = associatedAppId;
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
			//TODO This would be a good point to request class bytecode from other modules, if so desired.
		}
	}

	public void appLoadClass(String name, byte[] classData) {

		if (!classes.containsKey(name) && !classBytes.containsKey(name)) {
			classBytes.put(name, classData);
		} else {
			LOGGER.debug("double loaded bytecode of class: " + name);
		}
	}

}

//
// Usage Notes:
//
//
// public class Main {
//
// /**
// * @param args
// * @throws IOException
// * @throws ClassNotFoundException
// */
// public static void main(String[] args) throws IOException, ClassNotFoundException {
// MyClassLoader classloader = new MyClassLoader();
// Class<?> loadedClass = null;
// Object actualClassObject = null;
// FunctionBlock finalProd;
//
//
//
// ////
// RandomAccessFile f = new RandomAccessFile("/home/cryptkiddy/current/TestClassName.class", "rw");
// byte[] b = new byte[(int)f.length()];
// f.read(b);
// f.close();
// classloader.appLoadClass("TestClassName", b);
//
// f = new RandomAccessFile("/home/cryptkiddy/current/SecondaryClassName.class", "rw");
// b = new byte[(int)f.length()];
// f.read(b);
// f.close();
// classloader.appLoadClass("SecondaryClassNamed", b);
//
// ////
//
//
// try {
// loadedClass = classloader.loadClass("TestClassName", true);
// } catch (ClassNotFoundException e) {
// System.err.println("Class bytecode not loaded.");
// throw e;
// }
//
// try {
// actualClassObject = loadedClass.newInstance();
// } catch (InstantiationException | IllegalAccessException e) {
// }
//
// if (!(actualClassObject instanceof FunctionBlock)) {
// throw new Error();
// }
// try {
// finalProd = (FunctionBlock) actualClassObject;
// } catch (ClassCastException e) {
// throw new ClassCastException("wrong superinterface of class");
// }
// finalProd.run();
//
// // ////////////
//
// Thread.currentThread().setContextClassLoader(classloader); //block one way of circumventing my classloader.
// Thread.currentThread().setUncaughtExceptionHandler(null); //??needed??
// SecurityManager secMan = new SecurityManager();
// System.setSecurityManager(secMan);
// // btw:my class loader must be the one loading a class X if I want every
// // classload of X to pass through my loader. Otherwise use the method
// // above.
//
// }
//
// }

