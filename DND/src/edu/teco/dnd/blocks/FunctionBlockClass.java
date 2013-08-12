package edu.teco.dnd.blocks;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.JavaClass;

public class FunctionBlockClass {
	private final Map<String, JavaClass> inputs;
	
	private final Map<String, JavaClass> outputs;
	
	private final Set<String> options;
	
	public FunctionBlockClass(final Map<String, JavaClass> inputs, final Map<String, JavaClass> outputs, final Set<String> options) {
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
}
