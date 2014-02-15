package edu.teco.dnd.module.messages.joinStartApp;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.Response;

/**
 * send by a module, when it was supposed to Start an application it joined before and succeed.
 * 
 * @author Marvin Marx
 * 
 */
public class StartApplicationAck extends Response {

	public static final String MESSAGE_TYPE = "start application ack";

	/**
	 * ID of the started App.
	 */
	public ApplicationID applicationID;

	/**
	 * 
	 * @param applicationID
	 *            UUID of the started App.
	 */
	public StartApplicationAck(ApplicationID applicationID) {
		this.applicationID = applicationID;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private StartApplicationAck() {
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
		StartApplicationAck other = (StartApplicationAck) obj;
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
		return "StartApplicationAck [applicationID=" + applicationID + ", getSourceUUID()=" + getSourceUUID() + ", getUUID()="
				+ getUUID() + "]";
	}

}
