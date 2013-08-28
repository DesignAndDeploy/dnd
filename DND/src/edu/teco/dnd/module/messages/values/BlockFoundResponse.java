package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

/**
 * response message send, when a block a certain module was looking for happens to be on this module.
 * 
 * @author Philipp
 * 
 */
public class BlockFoundResponse extends Response {

	public static String MESSAGE_TYPE = "block found";

	public final UUID moduleId;

	/**
	 * The UUID of the module the block we were looking for is on.
	 * 
	 * @param moduleId
	 */
	public BlockFoundResponse(UUID moduleId) {
		this.moduleId = moduleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((moduleId == null) ? 0 : moduleId.hashCode());
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
		BlockFoundResponse other = (BlockFoundResponse) obj;
		if (moduleId == null) {
			if (other.moduleId != null) {
				return false;
			}
		} else if (!moduleId.equals(other.moduleId)) {
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
		return "BlockFoundMessage[moduleId=" + moduleId + ", getSourceUUID()=" + getSourceUUID() + ", getUUID()="
				+ getUUID() + "]";
	}

}
