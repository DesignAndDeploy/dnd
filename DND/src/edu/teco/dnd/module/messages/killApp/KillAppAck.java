package edu.teco.dnd.module.messages.killApp;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.Response;

/**
 * send when a new Application is supposed to be started.
 * 
 * @author Marvin Marx
 * 
 */
public class KillAppAck extends Response {

	public static final String MESSAGE_TYPE = "kill ack";

	/**
	 * ID of the application that was supposed to be killed.
	 */
	public ApplicationID applicationID;

	/**
	 * 
	 * @param applicationID
	 *            ID of the application that was supposed to be killed.
	 */
	public KillAppAck(ApplicationID applicationID) {
		this.applicationID = applicationID;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private KillAppAck() {
		applicationID = null;
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
		result = prime * result + ((applicationID == null) ? 0 : applicationID.hashCode());
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
		KillAppAck other = (KillAppAck) obj;
		if (applicationID == null) {
			if (other.applicationID != null) {
				return false;
			}
		} else if (!applicationID.equals(other.applicationID)) {
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
		return "KillAppAck [applicationID=" + applicationID + ", getSourceUUID()=" + getSourceUUID() + ", getUUID()="
				+ getUUID() + "]";
	}

}
