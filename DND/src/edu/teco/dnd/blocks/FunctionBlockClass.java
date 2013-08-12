package edu.teco.dnd.blocks;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.bcel.classfile.JavaClass;

public class FunctionBlockClass {
	public static final Pattern SIMPLE_CLASS_NAME_PATTERN = Pattern.compile("^(?:.*\\.)([^.]*)$");
	
	private final JavaClass cls;
	
	private final Map<String, JavaClass> inputs;
	
	private final Map<String, JavaClass> outputs;
	
	private final Set<String> options;
	
	public FunctionBlockClass(final JavaClass cls, final Map<String, JavaClass> inputs, final Map<String, JavaClass> outputs, final Set<String> options) {
		this.cls = cls;
		this.inputs = Collections.unmodifiableMap(inputs);
		this.outputs = Collections.unmodifiableMap(outputs);
		this.options = Collections.unmodifiableSet(options);
	}
	
	public Map<String, JavaClass> getOutputs() {
		return this.outputs;
	}
	
	public Map<String, JavaClass> getInputs() {
		return this.inputs;
	}
	
	public Set<String> getOptions() {
		return this.options;
	}
	
	public String getClassName() {
		return cls.getClassName();
	}
	
	public String getSimpleClassName() {
		final Matcher matcher = SIMPLE_CLASS_NAME_PATTERN.matcher(getClassName());
		if (!matcher.find()) {
			return getClassName();
		}
		return matcher.group(0);
	}
	
	public JavaClass getFunctionBlockClass() {
		return this.cls;
	}
}
