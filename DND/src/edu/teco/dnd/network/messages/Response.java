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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((sourceUUID == null) ? 0 : sourceUUID.hashCode());
		final UUID uuid = getUUID();
		result = prime * result
				+ ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Response other = (Response) obj;
		if (sourceUUID == null) {
			if (other.sourceUUID != null)
				return false;
		} else if (!sourceUUID.equals(other.sourceUUID))
			return false;
		UUID uuid = getUUID();
		if (uuid == null) {
			if (other.getUUID() != null)
				return false;
		} else if (!uuid.equals(other.getUUID()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Response[uuid=" + getUUID() + ",sourceUUID=" + getSourceUUID() + "]";
	}
}
