package edu.teco.dnd.network.messages;

import java.util.UUID;

import edu.teco.dnd.network.ConnectionManager;

/**
 * This is a marker interface for Message that can be sent and/or received with a {@link ConnectionManager}. Each
 * Message has a {@link UUID}. This UUID must be unique (which means that no Message may be sent twice) or things will
 * break.
 */
public abstract class Message {
	private final UUID uuid;

	public Message(final UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Initializes a new Message with a random UUID.
	 */
	public Message() {
		this.uuid = UUID.randomUUID();
	}

	public UUID getUUID() {
		return this.uuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [uuid=" + uuid + "]";
	}
}
