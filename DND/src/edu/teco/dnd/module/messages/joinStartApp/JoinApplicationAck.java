package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

/**
 * send when the module received a request to join in a new Application and prepare for starting it and this was
 * successful.
 * 
 * @author Marvin Marx
 * 
 */
public class JoinApplicationAck extends Response {

	public static String MESSAGE_TYPE = "join application ack";

	/**
	 * UUID of the new Application.
	 */
	public final UUID appId;
	/**
	 * Human readable name of the new application.
	 */
	public final String name;

	/**
	 * 
	 * @param name
	 *            human readable name of the App joined.
	 * @param appId
	 *            id of the app started.
	 */
	public JoinApplicationAck(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}

	/**
	 * convenience constructor.
	 * 
	 * @param msg
	 *            the JoinAppMsg this is the reply to.
	 */
	public JoinApplicationAck(JoinApplicationMessage msg) {
		this.name = msg.name;
		this.appId = msg.appId;
	}

	/** for gson. */
	@SuppressWarnings("unused")
	private JoinApplicationAck() {
		name = null;
		appId = null;
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
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
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
		if (appId == null) {
			if (other.appId != null) {
				return false;
			}
		} else if (!appId.equals(other.appId)) {
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
		return "JoinApplicationAck [appId=" + appId + ", name=" + name + ", getSourceUUID()=" + getSourceUUID()
				+ ", getUUID()=" + getUUID() + "]";
	}

}
