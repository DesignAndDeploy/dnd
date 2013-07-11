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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Message other = (Message) obj;
		if (uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [uuid=" + uuid + "]";
	}
	
	
	
}
