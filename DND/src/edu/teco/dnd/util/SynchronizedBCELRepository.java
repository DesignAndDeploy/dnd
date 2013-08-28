package edu.teco.dnd.util;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;

/**
 * A version of Repository for concurrent access.
 * 
 * @author Philipp Adolf
 */
public class SynchronizedBCELRepository implements Repository {
	private static final long serialVersionUID = -52698052303343161L;
	
	private final Repository realRepository;

	public SynchronizedBCELRepository(final Repository realRepository) {
		this.realRepository = realRepository;
	}

	@Override
	public synchronized void storeClass(final JavaClass clazz) {
		realRepository.storeClass(clazz);
	}

	@Override
	public synchronized void removeClass(final JavaClass clazz) {
		realRepository.removeClass(clazz);
	}

	@Override
	public synchronized JavaClass findClass(final String className) {
		return realRepository.findClass(className);
	}

	@Override
	public synchronized JavaClass loadClass(final String className) throws ClassNotFoundException {
		return realRepository.loadClass(className);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public synchronized JavaClass loadClass(final Class clazz) throws ClassNotFoundException {
		return realRepository.loadClass(clazz);
	}

	@Override
	public synchronized void clear() {
		realRepository.clear();
	}

	@Override
	public synchronized ClassPath getClassPath() {
		return realRepository.getClassPath();
	}
}
