package edu.teco.dnd.module;

import java.util.UUID;

/**
 * An ID for a {@link Module}.
 */
public class ModuleID {
	private final UUID id;

	/**
	 * Initializes a new ModuleID with a given UUID.
	 * 
	 * @param id
	 *            the UUID to use
	 */
	public ModuleID(final UUID id) {
		this.id = id;
	}

	/**
	 * Initializes a new random ModuleID.
	 */
	public ModuleID() {
		this(UUID.randomUUID());
	}

	public UUID getUUID() {
		return id;
	}

	/**
	 * Checks to see if this ModuleID belongs to a Master for another ModuleID. A ModuleID is a master if the
	 * {@link #getUUID() UUID} is smaller.
	 * 
	 * @param other
	 *            the other ModuleID to check. Must not be null.
	 * @return true if this ModuleID is a Master for the other one
	 */
	public boolean isMasterFor(final ModuleID other) {
		return id.compareTo(other.id) < 0;
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
		ModuleID other = (ModuleID) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ModuleID[" + id + "]";
	}
}
