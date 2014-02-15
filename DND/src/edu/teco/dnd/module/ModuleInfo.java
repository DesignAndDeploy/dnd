package edu.teco.dnd.module;

import java.util.HashMap;

import edu.teco.dnd.module.config.BlockTypeHolder;

/**
 * Represents the configuration/setup of a ModuleInfo. Used for transporting said information.
 * 
 * @author jung
 * 
 */
public class ModuleInfo {
	private ModuleID moduleID;
	private String name;
	private String location;
	private BlockTypeHolder holder;

	/**
	 * Creates a new representation of a module.
	 * 
	 * @param moduleID
	 *            the ID of the module.
	 * @param name
	 *            Name of the module.
	 * @param holder
	 *            BlockTypeHolder of the module.
	 */
	public ModuleInfo(ModuleID moduleID, String name, String location, BlockTypeHolder holder) {
		this.moduleID = moduleID;
		this.name = name;
		this.location = location;
		this.holder = holder;
	}

	public ModuleInfo(final ModuleID moduleID) {
		this(moduleID, null, null, null);
	}

	/**
	 * Returns the BlockTypeHolder of this module.
	 * 
	 * @return BlockTypeHolder of this module.
	 */
	public BlockTypeHolder getHolder() {
		return this.holder;
	}

	/** @return the ID of the module represented by this. */
	public ModuleID getID() {
		return moduleID;
	}

	/**
	 * Returns Map from available Types(RegExes) to amount of available slots.
	 * 
	 * @return Map from Types to amount of free slots for this type
	 */
	public HashMap<String, Integer> getTypes() {
		return holder.getAllAllowedChildTypes();
	}

	/** @return human readable name of module. */
	public String getName() {
		return name;
	}

	/** @return location this module gives. used to limit blocks allowed to run on it. */
	public String getLocation() {
		return location;
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
		result = prime * result + ((holder == null) ? 0 : holder.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((moduleID == null) ? 0 : moduleID.hashCode());
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
		ModuleInfo other = (ModuleInfo) obj;
		if (holder == null) {
			if (other.holder != null) {
				return false;
			}
		} else if (!holder.equals(other.holder)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (moduleID == null) {
			if (other.moduleID != null) {
				return false;
			}
		} else if (!moduleID.equals(other.moduleID)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ModuleInfo[moduleID=" + moduleID + ",name=" + name + ",location=" + location + "]";
	}
}
