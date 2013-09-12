package edu.teco.dnd.module;

import java.util.UUID;

/**
 * This class represents a unique ID for each instance of a function block. That way, all blocks can have the same UUID
 * as the function block model they derived from, but still be distinguished by their BlockID. The BlockID contains the
 * unique combination of the UUID of the function block and the UUID of the application
 * 
 * @author Alisa Jung
 * 
 */
public class BlockID{
	private UUID blockID;
	private UUID appID;

	/**
	 * Creates a new BlocKID to uniquely identify a function block.
	 * 
	 * @param blockUUID
	 *            UUID of the function block.
	 * @param appID
	 *            ID of the application this instance of the block belongs to.
	 */
	public BlockID(UUID blockUUID, UUID appID) {
		this.blockID = blockUUID;
		this.appID = appID;
	}

	@Override
	public String toString(){
		return "blablubb";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appID == null) ? 0 : appID.hashCode());
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
		BlockID other = (BlockID) obj;
		if (appID == null) {
			if (other.appID != null)
				return false;
		} else if (!appID.equals(other.appID))
			return false;
		if (blockID == null) {
			if (other.blockID != null)
				return false;
		} else if (!blockID.equals(other.blockID))
			return false;
		return true;
	}
	
}
