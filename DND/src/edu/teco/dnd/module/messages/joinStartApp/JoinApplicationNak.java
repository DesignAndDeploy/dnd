package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

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
	 * human readable name of the application.
	 */
	public String name;
	/**
	 * UUID of the application that was supposed to be joined.
	 */
	public UUID appId;

	/**
	 * 
	 * @param name
	 *            human readable name of the application.
	 * @param appId
	 *            UUID of the application that was supposed to be joined.
	 */
	public JoinApplicationNak(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}

	/**
	 * convenience constructor.
	 * 
	 * @param msg
	 *            the message that triggered the failure.
	 */
	public JoinApplicationNak(JoinApplicationMessage msg) {
		this.name = msg.name;
		this.appId = msg.appId;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private JoinApplicationNak() {
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
		JoinApplicationNak other = (JoinApplicationNak) obj;
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
		return "JoinApplicationNak [name=" + name + ", appId=" + appId + ", getSourceUUID()=" + getSourceUUID()
				+ ", getUUID()=" + getUUID() + "]";
	}

}
