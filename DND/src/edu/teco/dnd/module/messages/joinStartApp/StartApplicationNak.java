package edu.teco.dnd.module.messages.joinStartApp;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.Response;

/**
 * send when a new Application was supposed to be started and it failed.
 * 
 * @author Marvin Marx
 * 
 */

public class StartApplicationNak extends Response {

	public static final String MESSAGE_TYPE = "start application nak";

	/**
	 * ID of the app supposed to be started.
	 */
	public ApplicationID applicationID;

	/**
	 * 
	 * @param applicationID
	 *            the application supposed to be started.
	 */
	public StartApplicationNak(ApplicationID applicationID) {
		this.applicationID = applicationID;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private StartApplicationNak() {
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
		StartApplicationNak other = (StartApplicationNak) obj;
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
		return "JoinApplicationNak [applicationID=" + applicationID + ", getSourceUUID()=" + getSourceUUID()
				+ ", getUUID()=" + getUUID() + "]";
	}

}
