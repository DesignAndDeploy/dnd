package edu.teco.dnd.module;

import java.util.UUID;

public class ModuleID {
	private final UUID id;
	
	public ModuleID(final UUID id) {
		this.id = id;
	}
	
	public ModuleID() {
		this(UUID.randomUUID());
	}
	
	public UUID getUUID() {
		return id;
	}

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
