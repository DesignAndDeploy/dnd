package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

/**
 * send when a new Application is supposed to be started.
 * 
 * @author Marvin Marx
 * 
 */

public class JoinApplicationNak extends Response {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "startAppNak";

	public String name;
	public UUID appId;

	public JoinApplicationNak(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}

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

	/* (non-Javadoc)
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

	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JoinApplicationNak [name=" + name + ", appId=" + appId + ", getSourceUUID()=" + getSourceUUID()
				+ ", getUUID()=" + getUUID() + "]";
	}
	
	
}
