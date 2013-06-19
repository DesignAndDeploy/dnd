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
	 * @param uuid the Message UUID
	 * @param applicationUUID the Application UUID
	 */
	public ApplicationSpecificMessage(final UUID uuid, final UUID applicationUUID) {
		super(uuid);
		this.applicationUUID = applicationUUID;
	}
	
	/**
	 * Initializes a new ApplicationSpecificMessage with a given Application UUID.
	 * 
	 * @param applicationUUID the Application UUID
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
}
