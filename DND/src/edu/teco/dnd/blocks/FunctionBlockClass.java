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
	
	private final String blockType;
	
	private final long updateInterval;
	
	private final Map<String, JavaClass> inputs;
	
	private final Map<String, JavaClass> outputs;
	
	private final Set<String> options;
	
	public FunctionBlockClass(final JavaClass cls, final String blockType, final Long updateInterval, final Map<String, JavaClass> inputs, final Map<String, JavaClass> outputs, final Set<String> options) {
		this.cls = cls;
		this.inputs = Collections.unmodifiableMap(inputs);
		this.outputs = Collections.unmodifiableMap(outputs);
		this.options = Collections.unmodifiableSet(options);
		this.blockType = blockType;
		this.updateInterval = updateInterval == null ? Long.MIN_VALUE : updateInterval;
	}
	
	public String getBlockType() {
		return this.blockType;
	}
	
	public long getUpdateInterval() {
		return this.updateInterval;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((blockType == null) ? 0 : blockType.hashCode());
		result = prime * result + ((cls == null) ? 0 : cls.hashCode());
		result = prime * result + ((inputs == null) ? 0 : inputs.hashCode());
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result + ((outputs == null) ? 0 : outputs.hashCode());
		result = prime * result
				+ (int) (updateInterval ^ (updateInterval >>> 32));
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
		if (blockType == null) {
			if (other.blockType != null)
				return false;
		} else if (!blockType.equals(other.blockType))
			return false;
		if (cls == null) {
			if (other.cls != null)
				return false;
		} else if (!cls.equals(other.cls))
			return false;
		if (inputs == null) {
			if (other.inputs != null)
				return false;
		} else if (!inputs.equals(other.inputs))
			return false;
		if (options == null) {
			if (other.options != null)
				return false;
		} else if (!options.equals(other.options))
			return false;
		if (outputs == null) {
			if (other.outputs != null)
				return false;
		} else if (!outputs.equals(other.outputs))
			return false;
		if (updateInterval != other.updateInterval)
			return false;
		return true;
	}
}
