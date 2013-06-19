package edu.teco.dnd.network.messages;

import java.util.UUID;

/**
 * This is a marker interface for Message that will be send.
 * 
 * @author Philipp Adolf
 */
public abstract class Message {
	/**
	 * The UUID of this Message.
	 */
	private final UUID uuid;
	
	/**
	 * Initializes a new Message with a given UUID.
	 * 
	 * @param uuid the UUID to use for this Message
	 */
	public Message(final UUID uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * Initializes a new Message with a random UUID.
	 */
	public Message() {
		this.uuid = UUID.randomUUID();
	}
	
	/**
	 * Returns the UUID of the Message.
	 * 
	 * @return
	 */
	public UUID getUUID() {
		return this.uuid;
	}
}
