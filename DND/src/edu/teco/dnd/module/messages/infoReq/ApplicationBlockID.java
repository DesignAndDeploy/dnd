package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.module.ApplicationID;

/**
 * This class represents a unique ID for each instance of a function block. That way, all blocks can have the same UUID
 * as the function block model they derived from, but still be distinguished by their ApplicationBlockID. The
 * ApplicationBlockID contains the unique combination of the UUID of the function block and the UUID of the application
 * 
 * @author Alisa Jung
 * 
 */
public class ApplicationBlockID {
	private UUID blockID;
	private ApplicationID applicationID;

	/**
	 * Creates a new BlocKID to uniquely identify a function block.
	 * 
	 * @param blockUUID
	 *            UUID of the function block.
	 * @param applicationID
	 *            ID of the application this instance of the block belongs to.
	 */
	public ApplicationBlockID(UUID blockUUID, ApplicationID applicationID) {
		this.blockID = blockUUID;
		this.applicationID = applicationID;
	}

	public UUID getBlockUUID() {
		return this.blockID;
	}

	public ApplicationID getApplicationID() {
		return this.applicationID;
	}

	@Override
	public String toString() {
		return "blablubb";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((applicationID == null) ? 0 : applicationID.hashCode());
		result = prime * result + ((blockID == null) ? 0 : blockID.hashCode());
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
		ApplicationBlockID other = (ApplicationBlockID) obj;
		if (applicationID == null) {
			if (other.applicationID != null)
				return false;
		} else if (!applicationID.equals(other.applicationID))
			return false;
		if (blockID == null) {
			if (other.blockID != null)
				return false;
		} else if (!blockID.equals(other.blockID))
			return false;
		return true;
	}

}
