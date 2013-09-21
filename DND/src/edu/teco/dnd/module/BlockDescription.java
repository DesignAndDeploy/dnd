package edu.teco.dnd.module;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import edu.teco.dnd.blocks.ValueDestination;

/**
 * Describes an instance of a block. Used to create FunctionBlocks in {@link Application}.
 * 
 * @author Philipp Adolf
 */
public class BlockDescription {
	public final String blockClassName;
	public final String blockName;
	public final UUID blockUUID;
	public final Map<String, String> options;
	public final Map<String, Set<ValueDestination>> outputs;
	public final int blockTypeHolderId;

	/**
	 * 
	 * @param blockClassName
	 *            fully qualified class name of the block.
	 * @param blockUUID
	 *            UUID this block will receive.
	 * @param options
	 *            options that should be set on this block.
	 * @param outputs
	 *            the outputs this block sends values from.
	 * @param blockTypeHolderId
	 *            ID of the assigned BlockTypeHolder, aka where to decrease the allowed blocks count.
	 */
	public BlockDescription(final String blockClassName, final String blockName, final UUID blockUUID, final Map<String, String> options,
			final Map<String, Set<ValueDestination>> outputs, final int blockTypeHolderId) {
		this.blockClassName = blockClassName;
		this.blockName = blockName;
		this.blockUUID = blockUUID;
		this.options = Collections.unmodifiableMap(new HashMap<String, String>(options));
		final Map<String, Set<ValueDestination>> modifiableOutputs =
				new HashMap<String, Set<ValueDestination>>();
		for (final Entry<String, Set<ValueDestination>> entry : outputs.entrySet()) {
			modifiableOutputs.put(entry.getKey(),
					Collections.unmodifiableSet(new HashSet<ValueDestination>(entry.getValue())));
		}
		this.outputs = Collections.unmodifiableMap(modifiableOutputs);
		this.blockTypeHolderId = blockTypeHolderId;
	}
}
