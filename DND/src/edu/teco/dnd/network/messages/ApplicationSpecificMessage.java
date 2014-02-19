package edu.teco.dnd.network.messages;

import java.util.UUID;

import edu.teco.dnd.module.ApplicationID;

/**
 * A Message that is specific to an application running on the module.
 * 
 * @see Message
 */
public abstract class ApplicationSpecificMessage extends Message {
	private final ApplicationID applicationID;

	/**
	 * Initializes a new ApplicationSpecificMessage with a given {@link Message} UUID and ApplicationID.
	 * 
	 * @param uuid
	 *            the Message UUID
	 * @param applicationID
	 *            the ApplicationID
	 */
	public ApplicationSpecificMessage(final UUID uuid, final ApplicationID applicationID) {
		super(uuid);
		this.applicationID = applicationID;
	}

	/**
	 * Initializes a new ApplicationSpecificMessage with a given {@link ApplicationID} and a random Message UUID.
	 * 
	 * @param applicationID
	 *            the ApplicationID
	 */
	public ApplicationSpecificMessage(final ApplicationID applicationID) {
		super();
		this.applicationID = applicationID;
	}

	/**
	 * Returns the ID of the application this Message should be delivered to
	 * 
	 * @return the ID of the application this Message should be delivered to
	 */
	public ApplicationID getApplicationID() {
		return applicationID;
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
		ApplicationSpecificMessage other = (ApplicationSpecificMessage) obj;
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
		return "ApplicationSpecificMessage [applicationUUID=" + applicationID + ", getUUID()=" + getUUID() + "]";
	}
}
