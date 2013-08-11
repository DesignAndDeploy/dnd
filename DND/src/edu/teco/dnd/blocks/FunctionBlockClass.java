package edu.teco.dnd.blocks;

import java.util.Collections;
import java.util.Map;

import org.apache.bcel.classfile.JavaClass;

public class FunctionBlockClass {
	private final Map<String, JavaClass> inputs;
	
	private final Map<String, JavaClass> outputs;
	
	public FunctionBlockClass(final Map<String, JavaClass> inputs, final Map<String, JavaClass> outputs) {
		this.inputs = Collections.unmodifiableMap(inputs);
		this.outputs = Collections.unmodifiableMap(outputs);
	}
	
	public Map<String, JavaClass> getOutputs() {
		return this.outputs;
	}
}
