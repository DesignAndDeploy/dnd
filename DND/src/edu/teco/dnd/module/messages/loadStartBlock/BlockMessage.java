package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class BlockMessage extends ApplicationSpecificMessage {

	public static String MESSAGE_TYPE = "block";

	public FunctionBlock block;

	public BlockMessage(UUID uuid, UUID appId, FunctionBlock funBlock) {
		super(uuid, appId);
		this.block = funBlock;
	}

	public BlockMessage(UUID appId, FunctionBlock funBlock) {
		super(appId);
		this.block = funBlock;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((block == null) ? 0 : block.hashCode());
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
		if (block == null) {
			if (other.block != null)
				return false;
		} else if (!block.equals(other.block))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BlockMessage[block=" + block + ", getApplicationID()=" + getApplicationID() + ", getUUID()="
				+ getUUID() + "]";
	}
}
