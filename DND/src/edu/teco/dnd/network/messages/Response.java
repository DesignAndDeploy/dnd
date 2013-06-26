package edu.teco.dnd.network.messages;

import java.util.UUID;

import com.google.gson.annotations.SerializedName;

/**
 * A Message that is sent as a Response for another Message.
 *
 * @author Philipp Adolf
 */
public abstract class Response extends Message {
	/**
	 * The UUID of the Message this is a response to.
	 */
	@SerializedName("sourceuuid")
	private UUID sourceUUID = null;
	
	/**
	 * Initializes a new Response.
	 * 
	 * @param sourceUUID the UUID of the Message this is a response to
	 * @param uuid the UUID for this Message
	 */
	public Response(final UUID sourceUUID, final UUID uuid) {
		super(uuid);
		this.sourceUUID = sourceUUID;
	}
	
	/**
	 * Initializes a new Response.
	 * 
	 * @param sourceUUID the UUID of the Message this is a response to
	 */
	public Response(final UUID sourceUUID) {
		super();
		this.sourceUUID = sourceUUID;
	}
	
	/**
	 * Initializes a new Response without a UUID.
	 */
	public Response() {
		super();
	}
	
	/**
	 * Returns the UUID of the Message this is a response to.
	 * 
	 * @return the UUID of the Message this is a response to
	 */
	public UUID getSourceUUID() {
		return this.sourceUUID;
	}
	
	/**
	 * Sets the UUID of the Message this is a response to.
	 * 
	 * @param sourceUUID the UUID of the Message this is a response to
	 */
	public void setSourceUUID(final UUID sourceUUID) {
		this.sourceUUID = sourceUUID;
	}
}
