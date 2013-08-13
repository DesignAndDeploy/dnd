package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import edu.teco.dnd.blocks.ValueDestination;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class BlockMessage extends ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "block";

	public final String blockClass;

	public final UUID blockUUID;

	public final Map<String, String> options;

	public final Map<String, Collection<ValueDestination>> outputs;

	public BlockMessage(UUID uuid, UUID appId, String blockClass, UUID blockUUID, Map<String, String> options,
			Map<String, Collection<ValueDestination>> outputs) {
		super(uuid, appId);
		this.blockClass = blockClass;
		this.blockUUID = blockUUID;
		this.options = new HashMap<String, String>(options);
		this.outputs = new HashMap<String, Collection<ValueDestination>>();
		for (final Entry<String, Collection<ValueDestination>> output : outputs.entrySet()) {
			final Collection<ValueDestination> destinations = output.getValue();
			if (destinations != null) {
				this.outputs.put(output.getKey(), new HashSet<ValueDestination>(destinations));
			}
		}
	}

	public BlockMessage(UUID appId, String blockClass, UUID blockUUID, Map<String, String> options,
			Map<String, Collection<ValueDestination>> outputs) {
		super(appId);
		this.blockClass = blockClass;
		this.blockUUID = blockUUID;
		this.options = new HashMap<String, String>(options);
		this.outputs = new HashMap<String, Collection<ValueDestination>>();
		for (final Entry<String, Collection<ValueDestination>> output : outputs.entrySet()) {
			final Collection<ValueDestination> destinations = output.getValue();
			if (destinations != null) {
				this.outputs.put(output.getKey(), new HashSet<ValueDestination>(destinations));
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((blockClass == null) ? 0 : blockClass.hashCode());
		result = prime * result + ((blockUUID == null) ? 0 : blockUUID.hashCode());
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockMessage other = (BlockMessage) obj;
		if (blockClass == null) {
			if (other.blockClass != null)
				return false;
		} else if (!blockClass.equals(other.blockClass))
			return false;
		if (blockUUID == null) {
			if (other.blockUUID != null)
				return false;
		} else if (!blockUUID.equals(other.blockUUID))
			return false;
		if (options == null) {
			if (other.options != null)
				return false;
		} else if (!options.equals(other.options))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BlockMessage[uuid=" + getUUID() + ",blockClass=" + blockClass + ", blockUUID=" + blockUUID
				+ ", options=" + options + "]";
	}
}
