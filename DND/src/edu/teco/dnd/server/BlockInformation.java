package edu.teco.dnd.server;

import java.util.UUID;

public class BlockInformation {
	private final UUID id;
	private final String name;
	private final String blockClass;
	private final UUID moduleID;
	
	public BlockInformation(final UUID id, final String name, final String blockClass, final UUID moduleID) {
		this.id = id;
		this.name = name;
		this.blockClass = blockClass;
		this.moduleID = moduleID;
	}
	
	public BlockInformation(final UUID blockID, final String name, final String blockClass) {
		this(blockID, name, blockClass, null);
	}
	
	public BlockInformation(final UUID blockID) {
		this(blockID, null, null, null);
	}
	
	public UUID getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getBlockClass() {
		return blockClass;
	}
	
	public UUID getModuleID() {
		return moduleID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blockClass == null) ? 0 : blockClass.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((moduleID == null) ? 0 : moduleID.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		BlockInformation other = (BlockInformation) obj;
		if (blockClass == null) {
			if (other.blockClass != null)
				return false;
		} else if (!blockClass.equals(other.blockClass))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (moduleID == null) {
			if (other.moduleID != null)
				return false;
		} else if (!moduleID.equals(other.moduleID))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String string = "BlockInformation[id=" + id;
		if (name != null) {
			string += ",name=" + name;
		}
		if (blockClass != null) {
			string += ",blockClass=" + blockClass;
		}
		if (moduleID != null) {
			 string += ",moduleID=" + moduleID;
		}
		return string + "]";
	}
}
