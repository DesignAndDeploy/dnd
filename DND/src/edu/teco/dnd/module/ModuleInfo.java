package edu.teco.dnd.module;

import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.messages.infoReq.ModuleInfoMessage;

/**
 * Contains information describing a {@link Module}.
 * 
 * @see ModuleInfoMessage
 */
public class ModuleInfo {
	private ModuleID moduleID;
	private String name;
	private String location;
	private BlockTypeHolder holder;

	/**
	 * Creates a ModuleInfo that will store information about a {@link Module}.
	 * 
	 * @param moduleID
	 *            the ID of the Module
	 * @param name
	 *            the name of the Module
	 * @param location
	 *            the location of the Module
	 * @param holder
	 *            the root BlockTypeHolder of the Module
	 */
	public ModuleInfo(ModuleID moduleID, String name, String location, BlockTypeHolder holder) {
		this.moduleID = moduleID;
		this.name = name;
		this.location = location;
		this.holder = holder;
	}

	/**
	 * Initializes a ModuleInfo with only a ModuleID. All other fields will be set to <code>null</code>.
	 * 
	 * @param moduleID
	 *            the ModuleID to use
	 */
	public ModuleInfo(final ModuleID moduleID) {
		this(moduleID, null, null, null);
	}

	public BlockTypeHolder getHolder() {
		return holder;
	}

	public ModuleID getID() {
		return moduleID;
	}

	public String getName() {
		return name;
	}

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
