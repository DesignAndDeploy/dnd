package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class WhoHasBlockMessage extends ApplicationSpecificMessage {
	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "who has block";
	public final UUID blockId;

	public WhoHasBlockMessage(UUID appId, UUID blockId) {
		super(appId);
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
