package edu.teco.dnd.module.messages.joinStartApp;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.Response;

/**
 * send when the module received a request to join in a new Application and prepare for starting it and this was
 * successful.
 * 
 * @author Marvin Marx
 * 
 */
public class JoinApplicationAck extends Response {

	public static final String MESSAGE_TYPE = "join application ack";

	/**
	 * ID of the new Application.
	 */
	public final ApplicationID applicationID;

	/**
	 * Human readable name of the new application.
	 */
	public final String name;

	/**
	 * 
	 * @param name
	 *            human readable name of the App joined.
	 * @param applicationID
	 *            id of the app started.
	 */
	public JoinApplicationAck(String name, ApplicationID applicationID) {
		this.name = name;
		this.applicationID = applicationID;
	}

	/**
	 * convenience constructor.
	 * 
	 * @param msg
	 *            the JoinAppMsg this is the reply to.
	 */
	public JoinApplicationAck(JoinApplicationMessage msg) {
		this.name = msg.name;
		this.applicationID = msg.applicationID;
	}

	/** for gson. */
	@SuppressWarnings("unused")
	private JoinApplicationAck() {
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
		JoinApplicationAck other = (JoinApplicationAck) obj;
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
		return "JoinApplicationAck [applicationID=" + applicationID + ", name=" + name + ", getSourceUUID()="
				+ getSourceUUID() + ", getUUID()=" + getUUID() + "]";
	}

}
