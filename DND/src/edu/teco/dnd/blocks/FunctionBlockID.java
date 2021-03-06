package edu.teco.dnd.blocks;

import java.util.UUID;

public class FunctionBlockID {
	private final UUID id;

	public FunctionBlockID(final UUID id) {
		this.id = id;
	}

	public FunctionBlockID() {
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
		FunctionBlockID other = (FunctionBlockID) obj;
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
