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

// TODO: check if performance can be improved by caching block type, inputs, outputs, etc
public class FunctionBlockClass {
	public static final Pattern SIMPLE_CLASS_NAME_PATTERN = Pattern.compile("^(?:.*\\.)([^.]*)$");

	private static final Pattern GENERIC_ARGUMENT_PATTERN = Pattern.compile("<L([^;]*);");

	private final JavaClass blockClass;

	private final Repository repository;

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

	private boolean isInputField(final Field field) throws ClassNotFoundException {
		return !field.isStatic() && isInput(field.getType());
	}

	private boolean isInput(final Type type) throws ClassNotFoundException {
		return type instanceof ObjectType && Input.class.getName().equals(((ObjectType) type).getClassName());
	}

	private JavaClass getInputType(final Field field) throws ClassNotFoundException {
		return getClassOfGenericArgument(field);
	}
	
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
	
	private boolean isOutputField(final Field field) throws ClassNotFoundException {
		return !field.isStatic() && isOutput(field.getType());
	}
	
	private boolean isOutput(final Type type) throws ClassNotFoundException {
		return type instanceof ObjectType && Output.class.getName().equals(((ObjectType) type).getClassName());
	}
	
	public JavaClass getOutputType(final Field field) throws ClassNotFoundException {
		return getClassOfGenericArgument(field);
	}

	public JavaClass getClassOfGenericArgument(final Field field) throws ClassNotFoundException {
		final Signature signature = getSignature(field);
		JavaClass argumentClass = null;
		if (signature != null) {
			argumentClass = repository.loadClass(getClassNameOfGenericArgument(signature));
		}
		return argumentClass;
	}

	public Signature getSignature(final Field field) {
		for (final Attribute attribute : field.getAttributes()) {
			if (attribute instanceof Signature) {
				return (Signature) attribute;
			}
		}
		return null;
	}

	public static String getClassNameOfGenericArgument(final Signature signature) {
		final Matcher matcher = GENERIC_ARGUMENT_PATTERN.matcher(signature.getSignature());
		matcher.find();
		String className = matcher.group(1);
		if (className != null) {
			className = className.replaceAll("/", ".");
		}
		return className;
	}

	public String getBlockType() throws ClassNotFoundException {
		final Field blockTypeField = getBlockTypeField();
		String blockType = null;
		if (blockTypeField != null) {
			blockType = getStringConstant(blockTypeField);
		}
		return blockType;
	}

	private Field getBlockTypeField() throws ClassNotFoundException {
		for (final Field field : new FieldIterable(blockClass)) {
			if (isBlockTypeField(field)) {
				return field;
			}
		}
		return null;
	}

	private boolean isBlockTypeField(final Field field) throws ClassNotFoundException {
		return isConstant(field) && isString(field.getType()) && "BLOCK_TYPE".equals(field.getName());
	}
	
	public Map<String, String> getOptions() throws ClassNotFoundException {
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
	
	private boolean isOptionField(final Field field) throws ClassNotFoundException {
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

	private boolean isString(final Type type) throws ClassNotFoundException {
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
	
	public String getClassName() {
		return blockClass.getClassName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blockClass == null) ? 0 : blockClass.hashCode());
		result = prime * result + ((repository == null) ? 0 : repository.hashCode());
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
		if (repository == null) {
			if (other.repository != null)
				return false;
		} else if (!repository.equals(other.repository))
			return false;
		return true;
	}
}
