package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

/**
 * send when the receiving module is supposed to join in a new Application and prepare for starting it
 * 
 * @author Marvin Marx
 * 
 */

public class JoinApplicationMessage extends Message {

	public static String MESSAGE_TYPE = "join application";

	/**
	 * UUID of the application to join in.
	 */
	public UUID appId;
	/**
	 * human readable name of the application to join
	 */
	public String name;

	public JoinApplicationMessage(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private JoinApplicationMessage() {
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
		JoinApplicationMessage other = (JoinApplicationMessage) obj;
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
		return "JoinApplicationMessage [appId=" + appId + ", name=" + name + ", getUUID()=" + getUUID() + "]";
	}

}
