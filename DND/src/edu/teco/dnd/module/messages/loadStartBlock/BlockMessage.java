package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlockID;
import edu.teco.dnd.blocks.InputDescription;
import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * Message informing a module that it is supposed to prepare a Block for starting in a given Application.
 * 
 */
public class BlockMessage extends ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "block";

	/**
	 * Class of the block to be started.
	 */
	public final String blockClass;

	/**
	 * Name of the block to be started.
	 */
	public final String blockName;

	/**
	 * ID the block is supposed to have after starting.
	 */
	public final FunctionBlockID blockID;

	/**
	 * options the block is supposed to have after starting.
	 */
	public final Map<String, String> options;

	/**
	 * outputs the block is supposed to have after starting.
	 */
	public final Map<String, Set<InputDescription>> outputs;

	/**
	 * place in the allowed block hierarchy this block is supposed to occupy.
	 */
	public int scheduleToId;

	/**
	 * 
	 * @param msgUuid
	 *            UUID of this message.
	 * @param applicationID
	 *            ID of the application this message is to be sent to.
	 * @param blockClass
	 *            the Class of the block to be started.
	 * @param blockName
	 *            the name of the block to be started.
	 * @param blockID
	 *            the UUID the block is supposed to have after starting.
	 * @param options
	 *            the options the block is supposed to have after starting.
	 * @param outputs
	 *            the outputs the block is supposed to have after starting.
	 * @param scheduledToId
	 *            place in the allowed block hierarchy this block is supposed to occupy.
	 */
	public BlockMessage(UUID msgUuid, ApplicationID applicationID, String blockClass, String blockName,
			FunctionBlockID blockID, Map<String, String> options, Map<String, Collection<InputDescription>> outputs,
			int scheduledToId) {
		super(msgUuid, applicationID);
		this.blockClass = blockClass;
		this.blockName = blockName;
		this.blockID = blockID;
		this.options = new HashMap<String, String>(options);
		this.outputs = new HashMap<String, Set<InputDescription>>();
		for (final Entry<String, Collection<InputDescription>> output : outputs.entrySet()) {
			final Collection<InputDescription> destinations = output.getValue();
			if (destinations != null) {
				this.outputs.put(output.getKey(), new HashSet<InputDescription>(destinations));
			}
		}
		this.scheduleToId = scheduledToId;
	}

	/**
	 * 
	 * @param applicationID
	 *            ID of the application this message is to be sent to.
	 * @param blockClass
	 *            the Class of the block to be started.
	 * @param blockName
	 *            the name of the block to be started.
	 * @param blockID
	 *            the ID the block is supposed to have after starting.
	 * @param options
	 *            the options the block is supposed to have after starting.
	 * @param outputs
	 *            the outputs the block is supposed to have after starting.
	 * @param scheduledToId
	 *            place in the allowed block hierarchy this block is supposed to occupy.
	 */
	public BlockMessage(ApplicationID applicationID, String blockClass, String blockName, FunctionBlockID blockID,
			Map<String, String> options, Map<String, Collection<InputDescription>> outputs, int scheduledToId) {
		super(applicationID);
		this.blockClass = blockClass;
		this.blockName = blockName;
		this.blockID = blockID;
		this.options = new HashMap<String, String>(options);
		this.outputs = new HashMap<String, Set<InputDescription>>();
		for (final Entry<String, Collection<InputDescription>> output : outputs.entrySet()) {
			final Collection<InputDescription> destinations = output.getValue();
			if (destinations != null) {
				this.outputs.put(output.getKey(), new HashSet<InputDescription>(destinations));
			}
		}
		this.scheduleToId = scheduledToId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((blockClass == null) ? 0 : blockClass.hashCode());
		result = prime * result + ((blockID == null) ? 0 : blockID.hashCode());
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result + ((outputs == null) ? 0 : outputs.hashCode());
		result = prime * result + scheduleToId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BlockMessage other = (BlockMessage) obj;
		if (blockClass == null) {
			if (other.blockClass != null) {
				return false;
			}
		} else if (!blockClass.equals(other.blockClass)) {
			return false;
		}
		if (blockID == null) {
			if (other.blockID != null) {
				return false;
			}
		} else if (!blockID.equals(other.blockID)) {
			return false;
		}
		if (options == null) {
			if (other.options != null) {
				return false;
			}
		} else if (!options.equals(other.options)) {
			return false;
		}
		if (outputs == null) {
			if (other.outputs != null) {
				return false;
			}
		} else if (!outputs.equals(other.outputs)) {
			return false;
		}
		if (scheduleToId != other.scheduleToId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BlockMessage [blockClass=" + blockClass + ", blockID=" + blockID + ", options=" + options
				+ ", outputs=" + outputs + ", scheduleToId=" + scheduleToId + "]";
	}
}
