package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class AppBlockIdFoundMessage extends ApplicationSpecificMessage {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "block id found";

	public final UUID modId;
	public final UUID funcBlock;

	public AppBlockIdFoundMessage(UUID appId, UUID modId, UUID funcBlock) {
		super(appId);
		this.modId = modId;
		this.funcBlock = funcBlock;
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
		result = prime * result + ((funcBlock == null) ? 0 : funcBlock.hashCode());
		result = prime * result + ((modId == null) ? 0 : modId.hashCode());
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
		AppBlockIdFoundMessage other = (AppBlockIdFoundMessage) obj;
		if (funcBlock == null) {
			if (other.funcBlock != null) {
				return false;
			}
		} else if (!funcBlock.equals(other.funcBlock)) {
			return false;
		}
		if (modId == null) {
			if (other.modId != null) {
				return false;
			}
		} else if (!modId.equals(other.modId)) {
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
		return "AppBlockIdFoundMessage [modId=" + modId + ", funcBlock=" + funcBlock + ", getApplicationID()="
				+ getApplicationID() + ", getUUID()=" + getUUID() + "]";
	}

}
