package edu.teco.dnd.blocks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;

import edu.teco.dnd.temperature.TemperatureLogicBlock;
import edu.teco.dnd.util.SynchronizedBCELRepository;

public class FunctionBlockFactory {
	private final Repository repository;

	private final ConcurrentMap<String, FunctionBlockClass> blocks =
			new ConcurrentHashMap<String, FunctionBlockClass>();

	public FunctionBlockFactory(final Repository repository) throws ClassNotFoundException {
		this.repository = new SynchronizedBCELRepository(repository);
	}

	public FunctionBlockFactory(final ClassPath classPath) throws ClassNotFoundException {
		this(SyntheticRepository.getInstance(classPath));
	}

	public FunctionBlockFactory(final String classPath) throws ClassNotFoundException {
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

	// FIXME: remove once no longer needed for testing
	public static void main(final String[] args) throws ClassNotFoundException {
		final FunctionBlockFactory factory = new FunctionBlockFactory(SyntheticRepository.getInstance());
		final FunctionBlockClass blockClass = factory.getFunctionBlockClass(TemperatureLogicBlock.class);
		System.out.println(blockClass.getOptions());
	}
}
