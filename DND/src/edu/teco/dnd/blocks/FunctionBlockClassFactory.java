package edu.teco.dnd.blocks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;

import edu.teco.dnd.util.SynchronizedBCELRepository;

public class FunctionBlockClassFactory {
	private final Repository repository;

	private final ConcurrentMap<String, FunctionBlockClass> blocks =
			new ConcurrentHashMap<String, FunctionBlockClass>();

	public FunctionBlockClassFactory(final Repository repository) throws ClassNotFoundException {
		this.repository = new SynchronizedBCELRepository(repository);
	}

	public FunctionBlockClassFactory(final ClassPath classPath) throws ClassNotFoundException {
		this(SyntheticRepository.getInstance(classPath));
	}

	public FunctionBlockClassFactory(final String classPath) throws ClassNotFoundException {
		this(new ClassPath(classPath));
	}

	public FunctionBlockClass getFunctionBlockClass(final Class<?> cls) throws ClassNotFoundException {
		return getFunctionBlockClass(cls.getCanonicalName());
	}

	public FunctionBlockClass getFunctionBlockClass(final String className) throws ClassNotFoundException {
		if (!blocks.containsKey(className)) {
			blocks.putIfAbsent(className, new FunctionBlockClass(repository, className));
		}
		return blocks.get(className);
	}
}
