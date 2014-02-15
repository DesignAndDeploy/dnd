package edu.teco.dnd.module;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.teco.dnd.blocks.FunctionBlockID;
import edu.teco.dnd.blocks.InputDescription;

/**
 * Describes an instance of a block. Used to create FunctionBlocks in {@link Application}.
 * 
 * @author Philipp Adolf
 */
public class BlockDescription {
	private final String blockClassName;
	private final String blockName;
	private final FunctionBlockID blockID;
	private final Map<String, String> options;
	private final Map<String, Set<InputDescription>> outputs;
	private final int blockTypeHolderID;

	/**
	 * 
	 * @param blockClassName
	 *            fully qualified class name of the block.
	 * @param blockID
	 *            ID this block will receive.
	 * @param options
	 *            options that should be set on this block.
	 * @param outputs
	 *            the outputs this block sends values from.
	 * @param blockTypeHolderID
	 *            ID of the assigned BlockTypeHolder, aka where to decrease the allowed blocks count.
	 */
	public BlockDescription(final String blockClassName, final String blockName, final FunctionBlockID blockID,
			final Map<String, String> options, final Map<String, Set<InputDescription>> outputs,
			final int blockTypeHolderID) {
		this.blockClassName = blockClassName;
		this.blockName = blockName;
		this.blockID = blockID;
		this.options = Collections.unmodifiableMap(new HashMap<String, String>(options));
		final Map<String, Set<InputDescription>> modifiableOutputs = new HashMap<String, Set<InputDescription>>();
		for (final Entry<String, Set<InputDescription>> entry : outputs.entrySet()) {
			modifiableOutputs.put(entry.getKey(),
					Collections.unmodifiableSet(new HashSet<InputDescription>(entry.getValue())));
		}
		this.outputs = Collections.unmodifiableMap(modifiableOutputs);
		this.blockTypeHolderID = blockTypeHolderID;
	}
	
	public String getBlockClassName() {
		return blockClassName;
	}
	
	public String getBlockName() {
		return blockName;
	}
	
	public FunctionBlockID getBlockID() {
		return blockID;
	}
	
	public Map<String, String> getOptions() {
		return options;
	}
	
	public Map<String, Set<InputDescription>> getOutputs() {
		return outputs;
	}
	
	public int getBlockTypeHolderID() {
		return blockTypeHolderID;
	}
}
