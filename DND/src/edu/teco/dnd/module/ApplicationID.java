package edu.teco.dnd.module;

import java.util.UUID;

/**
 * An ID for an {@link Application}.
 */
public class ApplicationID {
	private final UUID id;

	/**
	 * Creates an ApplicationID based on a given UUID.
	 * 
	 * @param id
	 *            the UUID to use
	 */
	public ApplicationID(final UUID id) {
		this.id = id;
	}

	/**
	 * Creates a new random ApplicationID.
	 */
	public ApplicationID() {
		this(UUID.randomUUID());
	}

	public UUID getUUID() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApplicationID other = (ApplicationID) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ApplicationID[" + id + "]";
	}
}
