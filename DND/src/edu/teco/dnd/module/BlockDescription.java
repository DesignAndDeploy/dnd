package edu.teco.dnd.module;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.FunctionBlockID;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.InputDescription;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.module.config.BlockTypeHolder;

/**
 * Describes an instance of a block. Used to create FunctionBlocks in {@link Application}.
 */
public class BlockDescription {
	private final String blockClassName;
	private final String blockName;
	private final FunctionBlockID blockID;
	private final Map<String, String> options;
	private final Map<String, Set<InputDescription>> outputs;
	private final int blockTypeHolderID;

	/**
	 * Initializes a new BlockDescription.
	 * 
	 * @param blockClassName
	 *            fully qualified class name of the block
	 * @param blockID
	 *            ID this block will have
	 * @param options
	 *            options that should be set on this block. See {@link FunctionBlock#init(Map)}
	 * @param outputs
	 *            a Map from {@link Output} name to a Set of {@link Input} (descriptions) the Output should sent to
	 * @param blockTypeHolderID
	 *            ID of the {@link BlockTypeHolder} the block should be stored under
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
