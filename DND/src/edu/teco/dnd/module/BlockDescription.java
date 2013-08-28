package edu.teco.dnd.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import edu.teco.dnd.blocks.ValueDestination;

/**
 * Describes an instance of a block. Used to create FunctionBlocks in {@link Application}.
 * 
 * @author Philipp Adolf
 */
public class BlockDescription {
	public final String blockClassName;
	public final UUID blockUUID;
	public final Map<String, String> options;
	public final Map<String, Collection<ValueDestination>> outputs;
	public final int blockTypeHolderID;

	public BlockDescription(final String blockClassName, final UUID blockUUID, final Map<String, String> options,
			final Map<String, Collection<ValueDestination>> outputs, final int blockTypeHolderID) {
		this.blockClassName = blockClassName;
		this.blockUUID = blockUUID;
		this.options = Collections.unmodifiableMap(new HashMap<String, String>(options));
		final Map<String, Collection<ValueDestination>> modifiableOutputs =
				new HashMap<String, Collection<ValueDestination>>();
		for (final Entry<String, Collection<ValueDestination>> entry : outputs.entrySet()) {
			modifiableOutputs.put(entry.getKey(),
					Collections.unmodifiableCollection(new ArrayList<ValueDestination>(entry.getValue())));
		}
		this.outputs = Collections.unmodifiableMap(modifiableOutputs);
		this.blockTypeHolderID = blockTypeHolderID;
	}
}
