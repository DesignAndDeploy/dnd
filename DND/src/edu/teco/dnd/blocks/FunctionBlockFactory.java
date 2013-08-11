package edu.teco.dnd.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Signature;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;

public class FunctionBlockFactory {
	// TODO: BCEL is not thread safe, wrap this repository
	private final Repository repository;
	
	private final JavaClass functionBlockClass;
	
	private final JavaClass outputClass;
	
	private final ConcurrentMap<String, FunctionBlockClass> blocks = new ConcurrentHashMap<String, FunctionBlockClass>();
	
	public FunctionBlockFactory(final Repository repository) throws ClassNotFoundException {
		this.repository = repository;
		this.functionBlockClass = repository.loadClass(FunctionBlock.class.getCanonicalName());
		this.outputClass = repository.loadClass(Output.class.getCanonicalName());
	}
	
	public FunctionBlockFactory(final ClassPath classPath) throws ClassNotFoundException {
		this(SyntheticRepository.getInstance(classPath));
	}
	
	public FunctionBlockFactory(final String classPath) throws ClassNotFoundException {
		this(new ClassPath(classPath));
	}
	
	public FunctionBlockClass getFunctionBlockClass(final String className) throws ClassNotFoundException {
		FunctionBlockClass block = blocks.get(className);
		if (block != null) {
			return block;
		}
		
		block = createFunctionBlock(className);
		final FunctionBlockClass oldBlock = blocks.putIfAbsent(className, block);
		if (oldBlock != null) {
			block = oldBlock;
		}
		return block;
	}
	
	
	private FunctionBlockClass createFunctionBlock(final String className) throws ClassNotFoundException {
		final JavaClass cls = repository.loadClass(className);
		if (!isSubclass(cls, functionBlockClass)) {
			throw new IllegalArgumentException(className + " is not a FunctionBlock");
		}
		final Map<String, JavaClass> inputs = new HashMap<String, JavaClass>();
		final Map<String, JavaClass> outputs = new HashMap<String, JavaClass>();
		JavaClass currentCls = cls;
		while (currentCls != null) {
			for (final Field field : currentCls.getFields()) {
				final Type type = field.getType();
				if (type instanceof ObjectType) {
					if (outputClass.getClassName().equals(((ObjectType) type).getClassName())) {
						for (final Attribute attribute : field.getAttributes()) {
							if (attribute instanceof Signature) {
								final JavaClass outputType = repository.loadClass(getGenericClassName(((Signature) attribute).getSignature()));
								outputs.put(field.getName(), outputType);
							}
						}
					}
				}
			}
			currentCls = currentCls.getSuperClass();
		}
		return new FunctionBlockClass(inputs, outputs);
	}
	
	private static String getGenericClassName(final String signature) {
		Matcher m = Pattern.compile("<L([^;]*);").matcher(signature);
		m.find();
		String clsName = m.group(1);
		return clsName.replaceAll("/", ".");
	}
	
	private static boolean isSubclass(final JavaClass cls, final JavaClass superCls) throws ClassNotFoundException {
		if (superCls.equals(cls)) {
			return true;
		}
		for (final JavaClass c : cls.getSuperClasses()) {
			if (superCls.equals(c)) {
				return true;
			}
		}
		return false;
	}

	public static void main(final String[] args) throws ClassNotFoundException {
		final FunctionBlockFactory factory = new FunctionBlockFactory(SyntheticRepository.getInstance());
		final FunctionBlockClass blockClass = factory.getFunctionBlockClass("edu.teco.dnd.temperature.TemperatureSensorBlock");
		System.out.println(blockClass.getOutputs());
	}
}
