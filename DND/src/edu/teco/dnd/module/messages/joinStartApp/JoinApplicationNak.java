package edu.teco.dnd.module.messages.joinStartApp;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.Response;

/**
 * send when a module failed to join an application.
 * 
 * @author Marvin Marx
 * 
 */

public class JoinApplicationNak extends Response {

	public static final String MESSAGE_TYPE = "join application nak";

	/**
	 * ID of the application that was supposed to be joined.
	 */
	public ApplicationID applicationID;

	/**
	 * human readable name of the application.
	 */
	public String name;

	/**
	 * 
	 * @param name
	 *            human readable name of the application.
	 * @param applicationID
	 *            UUID of the application that was supposed to be joined.
	 */
	public JoinApplicationNak(String name, ApplicationID applicationID) {
		this.name = name;
		this.applicationID = applicationID;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private JoinApplicationNak() {
		name = null;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		JoinApplicationNak other = (JoinApplicationNak) obj;
		if (applicationID == null) {
			if (other.applicationID != null) {
				return false;
			}
		} else if (!applicationID.equals(other.applicationID)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
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
		return "JoinApplicationNak [name=" + name + ", applicationID=" + applicationID + ", getSourceUUID()="
				+ getSourceUUID() + ", getUUID()=" + getUUID() + "]";
	}

}
