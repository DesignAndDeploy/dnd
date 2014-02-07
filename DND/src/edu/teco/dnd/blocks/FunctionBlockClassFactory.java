package edu.teco.dnd.blocks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;

import edu.teco.dnd.util.SynchronizedBCELRepository;

/**
 * A factory for statically inspecting {@link FunctionBlock}s. All methods are thread-safe.
 */
public class FunctionBlockClassFactory {
	private final Repository repository;

	private final ConcurrentMap<String, FunctionBlockClass> blocks =
			new ConcurrentHashMap<String, FunctionBlockClass>();

	/**
	 * Initializes a new FunctionBlockClassFactory with the given Repository. This factory will only be thread-safe if
	 * the Repository is not accessed at the same time as methods on this Factory are called.
	 * 
	 * @param repository
	 *            the Repository to use for loading classes
	 */
	public FunctionBlockClassFactory(final Repository repository) {
		this.repository = new SynchronizedBCELRepository(repository);
	}

	/**
	 * Initializes a new FunctionBlockClassFactory that will use the given ClassPath for searching classes.
	 * 
	 * @param classPath
	 *            the ClassPath to use
	 */
	public FunctionBlockClassFactory(final ClassPath classPath) {
		this(SyntheticRepository.getInstance(classPath));
	}

	/**
	 * Initializes a new FunctionBlockClassFactory that will use the given class path for searching classes.
	 * 
	 * @param classPath
	 *            the class path to use. The system property <code>path.separator</code> is used to separate the
	 *            individual entries
	 */
	public FunctionBlockClassFactory(final String classPath) {
		this(new ClassPath(classPath));
	}

	/**
	 * Returns the FunctionBlockClass for the given (loaded) class. This will only work if this Factory uses the same
	 * class path as the running VM.
	 * 
	 * @param cls
	 *            the Class to load
	 * @return the FunctionBlockClass for the given class
	 * @throws ClassNotFoundException
	 *             if the Class was not found
	 * @throws IllegalArgumentException
	 *             if the given class is not a FunctionBlock
	 */
	public FunctionBlockClass getFunctionBlockClass(final Class<?> cls) throws ClassNotFoundException {
		return getFunctionBlockClass(cls.getCanonicalName());
	}

	/**
	 * Returns the FunctionBlockClass for the given class name.
	 * 
	 * @param className
	 *            the name of the class to load
	 * @return the FunctionBlockClass for the class with the given name
	 * @throws ClassNotFoundException
	 *             if the class was not found
	 * @throws IllegalArgumentException
	 *             if the class is not a FunctionBlock
	 */
	public FunctionBlockClass getFunctionBlockClass(final String className) throws ClassNotFoundException {
		if (!blocks.containsKey(className)) {
			blocks.putIfAbsent(className, new FunctionBlockClass(repository, className));
		}
		return blocks.get(className);
	}
}
