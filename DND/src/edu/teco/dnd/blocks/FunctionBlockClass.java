package edu.teco.dnd.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Signature;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.Repository;

import edu.teco.dnd.util.FieldIterable;

/**
 * Represents a (statically inspected) {@link FunctionBlock} class. This method should not be instantiated directly,
 * instead use a {@link FunctionBlockClassFactory}.
 */
public class FunctionBlockClass {
	private static final Pattern GENERIC_ARGUMENT_PATTERN = Pattern.compile("<L([^;]*);");

	private final JavaClass blockClass;
	private final Repository repository;

	/**
	 * Initializes a new FunctionBlockClass. This should normally not be called directly, instead use a
	 * {@link FunctionBlockClassFactory}.
	 * 
	 * @param repository
	 *            the Repository that should be used to load the class
	 * @param className
	 *            the name of the class
	 * @throws ClassNotFoundException
	 *             if the class is not found
	 * @throws IllegalArgumentException
	 *             if the class is not a {@link FunctionBlock}
	 */
	public FunctionBlockClass(final Repository repository, final String className) throws ClassNotFoundException {
		this.repository = repository;
		this.blockClass = repository.loadClass(className);
		if (!isFunctionBlock(blockClass)) {
			throw new IllegalArgumentException("not a FunctionBlock class: " + className);
		}
	}

	private boolean isFunctionBlock(final JavaClass blockClass) throws ClassNotFoundException {
		return isSubclass(blockClass, getFunctionBlockClass()) && !blockClass.isAbstract();
	}

	private JavaClass getFunctionBlockClass() throws ClassNotFoundException {
		return repository.loadClass(FunctionBlock.class);
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

	/**
	 * Returns all inputs defined by the FunctionBlock. The Map returned uses the name of the input as the key and the
	 * class of the input as the value.
	 * 
	 * @return the inputs defined by the FunctionBlock. Maps from input name to input class
	 * @throws ClassNotFoundException
	 *             if any of the classes used by the inputs could not be loaded
	 */
	public Map<String, JavaClass> getInputs() throws ClassNotFoundException {
		final Map<String, JavaClass> inputs = new HashMap<String, JavaClass>();
		for (final Field field : new FieldIterable(blockClass)) {
			if (isInputField(field)) {
				final String name = field.getName();
				if (!inputs.containsKey(name)) {
					inputs.put(field.getName(), getInputType(field));
				}
			}
		}
		return inputs;
	}

	private boolean isInputField(final Field field) {
		return !field.isStatic() && isInput(field.getType());
	}

	private boolean isInput(final Type type) {
		return type instanceof ObjectType && Input.class.getName().equals(((ObjectType) type).getClassName());
	}

	private JavaClass getInputType(final Field field) throws ClassNotFoundException {
		return getClassOfGenericArgument(field);
	}

	/**
	 * Returns all inputs defined by the FunctionBlock. The Map returned uses the name of the input as the key and the
	 * class of the input as the value.
	 * 
	 * @return the inputs defined by the FunctionBlock. Maps from input name to input class
	 * @throws ClassNotFoundException
	 *             if any of the classes used by the inputs could not be loaded
	 */
	public Map<String, JavaClass> getOutputs() throws ClassNotFoundException {
		final Map<String, JavaClass> outputs = new HashMap<String, JavaClass>();
		for (final Field field : new FieldIterable(blockClass)) {
			if (isOutputField(field)) {
				final String name = field.getName();
				if (!outputs.containsKey(name)) {
					outputs.put(field.getName(), getOutputType(field));
				}
			}
		}
		return outputs;
	}

	private boolean isOutputField(final Field field) {
		return !field.isStatic() && isOutput(field.getType());
	}

	private boolean isOutput(final Type type) {
		return type instanceof ObjectType && Output.class.getName().equals(((ObjectType) type).getClassName());
	}

	private JavaClass getOutputType(final Field field) throws ClassNotFoundException {
		return getClassOfGenericArgument(field);
	}

	private JavaClass getClassOfGenericArgument(final Field field) throws ClassNotFoundException {
		final Signature signature = getSignature(field);
		JavaClass argumentClass = null;
		if (signature != null) {
			argumentClass = repository.loadClass(getClassNameOfGenericArgument(signature));
		}
		return argumentClass;
	}

	private Signature getSignature(final Field field) {
		for (final Attribute attribute : field.getAttributes()) {
			if (attribute instanceof Signature) {
				return (Signature) attribute;
			}
		}
		return null;
	}

	private static String getClassNameOfGenericArgument(final Signature signature) {
		final Matcher matcher = GENERIC_ARGUMENT_PATTERN.matcher(signature.getSignature());
		matcher.find();
		String className = matcher.group(1);
		if (className != null) {
			className = className.replaceAll("/", ".");
		}
		return className;
	}

	/**
	 * Returns the block type defined by the FunctionBlock. May be null.
	 * 
	 * @return the block type as defined by the FunctionBlock or null if not found
	 */
	public String getBlockType() {
		final Field blockTypeField = getBlockTypeField();
		String blockType = null;
		if (blockTypeField != null) {
			blockType = getStringConstant(blockTypeField);
		}
		return blockType;
	}

	private Field getBlockTypeField() {
		for (final Field field : new FieldIterable(blockClass)) {
			if (isBlockTypeField(field)) {
				return field;
			}
		}
		return null;
	}

	private boolean isBlockTypeField(final Field field) {
		return isConstant(field) && isString(field.getType()) && "BLOCK_TYPE".equals(field.getName());
	}

	/**
	 * Returns the options defined by the FunctionBlock. The result maps from option name to default value.
	 * 
	 * @return the options as defined by the FunctionBlock. Maps from option name to default value.
	 */
	public Map<String, String> getOptions() {
		final Map<String, String> options = new HashMap<String, String>();
		for (final Field field : new FieldIterable(blockClass)) {
			if (isOptionField(field)) {
				final String name = getOptionName(field);
				if (!options.containsKey(name)) {
					options.put(name, getDefaultOptionValue(field));
				}
			}
		}
		return options;
	}

	private boolean isOptionField(final Field field) {
		return isConstant(field) && isString(field.getType()) && field.getName().startsWith("OPTION_");
	}

	private String getOptionName(final Field field) {
		return field.getName().replaceFirst("^OPTION_", "");
	}

	private String getDefaultOptionValue(final Field field) {
		return getStringConstant(field);
	}

	private static boolean isConstant(final Field field) {
		return field.isStatic() && field.isFinal();
	}

	private boolean isString(final Type type) {
		return type instanceof ObjectType && String.class.getName().equals(((ObjectType) type).getClassName());
	}

	private String getStringConstant(final Field field) {
		String result = null;
		if (hasConstantValue(field)) {
			final ConstantPool constantPool = field.getConstantPool();
			final int constantIndex = field.getConstantValue().getConstantValueIndex();
			final String constantBytes = getStringConstantBytes(constantPool, constantIndex);
			result = Utility.convertString(constantBytes);
		}
		return result;
	}

	private static boolean hasConstantValue(final Field field) {
		return field.getConstantValue() != null;
	}

	private String getStringConstantBytes(final ConstantPool constantPool, final int constantIndex) {
		final ConstantString constantString = (ConstantString) constantPool.getConstant(constantIndex);
		final ConstantUtf8 constantUtf8 =
				(ConstantUtf8) constantPool.getConstant(constantString.getStringIndex(), Constants.CONSTANT_Utf8);
		return constantUtf8.getBytes();
	}

	/**
	 * Returns the name of the class represented by this object.
	 * 
	 * @return the name of the class represented by this object
	 */
	public String getClassName() {
		return blockClass.getClassName();
	}

	/**
	 * Creates a simplified class name. Currently only strips the package name, but may be improved later on to handle
	 * inner classes and similar cases.
	 * 
	 * @return a simplified class name
	 */
	public String getSimplifiedClassName() {
		String className = blockClass.getClassName();
		try {
			className = className.substring(className.lastIndexOf(".") + 1);
		} catch (NullPointerException e) {
		}
		return className;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blockClass == null) ? 0 : blockClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionBlockClass other = (FunctionBlockClass) obj;
		if (blockClass == null) {
			if (other.blockClass != null)
				return false;
		} else if (!blockClass.equals(other.blockClass))
			return false;
		return true;
	}
}
