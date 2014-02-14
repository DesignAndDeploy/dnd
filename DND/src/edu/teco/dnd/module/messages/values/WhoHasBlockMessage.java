package edu.teco.dnd.module.messages.values;

import edu.teco.dnd.blocks.FunctionBlockID;
import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * Message send to request the position of a FunctionBlock if it is unknown to the module.
 * 
 * @author Marvin Marx
 * 
 */
public class WhoHasBlockMessage extends ApplicationSpecificMessage {
	public static final String MESSAGE_TYPE = "who has block";
	/**
	 * ID of the Block being searched for.
	 */
	public final FunctionBlockID blockId;

	/**
	 * 
	 * @param applicationID
	 *            Id of the application executing the block.
	 * @param blockId
	 *            Id of the block being looked for.
	 */
	public WhoHasBlockMessage(ApplicationID applicationID, FunctionBlockID blockId) {
		super(applicationID);
		this.blockId = blockId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((blockId == null) ? 0 : blockId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		WhoHasBlockMessage other = (WhoHasBlockMessage) obj;
		if (blockId == null) {
			if (other.blockId != null) {
				return false;
			}
		} else if (!blockId.equals(other.blockId)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WhoHasBlockMessage [blockId=" + blockId + ", getApplicationID()=" + getApplicationID() + ", getUUID()="
				+ getUUID() + "]";
	}

}
