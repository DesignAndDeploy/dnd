package edu.teco.dnd.network.messages;

import java.util.UUID;

/**
 * A Message that is specific to an application running on the module.
 * 
 * @author Philipp Adolf
 */
public abstract class ApplicationSpecificMessage extends Message {
	/**
	 * The UUID of the Application this Message is intended for.
	 */
	private final UUID applicationUUID;

	/**
	 * Initializes a new ApplicationSpecificMessage with a given Message UUID and an Application UUID.
	 * 
	 * @param uuid
	 *            the Message UUID
	 * @param applicationUUID
	 *            the Application UUID
	 */
	public ApplicationSpecificMessage(final UUID uuid, final UUID applicationUUID) {
		super(uuid);
		this.applicationUUID = applicationUUID;
	}

	/**
	 * Initializes a new ApplicationSpecificMessage with a given Application UUID.
	 * 
	 * @param applicationUUID
	 *            the Application UUID
	 */
	public ApplicationSpecificMessage(final UUID applicationUUID) {
		super();
		this.applicationUUID = applicationUUID;
	}

	/**
	 * Returns the ID of the application this message should be delivered to
	 * 
	 * @return the ID of the application this message should be delivered to
	 */
	public UUID getApplicationID() {
		return applicationUUID;
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
		result = prime * result + ((applicationUUID == null) ? 0 : applicationUUID.hashCode());
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
		ApplicationSpecificMessage other = (ApplicationSpecificMessage) obj;
		if (applicationUUID == null) {
			if (other.applicationUUID != null) {
				return false;
			}
		} else if (!applicationUUID.equals(other.applicationUUID)) {
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
		return "ApplicationSpecificMessage [applicationUUID=" + applicationUUID + ", getUUID()=" + getUUID() + "]";
	}

}
