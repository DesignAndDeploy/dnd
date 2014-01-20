package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.network.messages.Response;

/**
 * Message containing information about the sending module (like name...).
 * 
 * @author Marvin Marx
 * 
 */
public class ModuleInfoMessage extends Response {

	public static final String MESSAGE_TYPE = "module info";
	/**
	 * a ModuleInfo class that encapsulates the information about the module.
	 */
	public final ModuleInfo module;

	/**
	 * construct a new module info message. (should usually not be used, unless it is desired to produce an exact
	 * duplication of an old message with equal UUIDs)
	 * 
	 * @param sourceMsgUuid
	 *            the msgUuid this is a reply to.
	 * @param msgUuid
	 *            the uuid of this message.
	 * @param module
	 *            the module with the appropriate informations set.
	 */
	public ModuleInfoMessage(final UUID sourceMsgUuid, final UUID msgUuid, final ModuleInfo module) {
		super(sourceMsgUuid, msgUuid);
		this.module = module;
	}

	/**
	 * construct a new ModuleInfo info message.
	 * 
	 * @param module
	 *            the encapsulated module information.
	 */
	public ModuleInfoMessage(final ModuleInfo module) {
		this.module = module;
	}

	/**
	 * @return the module with the encapsulated module information.
	 */
	public ModuleInfo getModule() {
		return this.module;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((module == null) ? 0 : module.hashCode());
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
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ModuleInfoMessage other = (ModuleInfoMessage) obj;
		if (module == null) {
			if (other.module != null) {
				return false;
			}
		} else if (!module.equals(other.module)) {
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
		return "ModuleInfoMessage [module=" + module + ", getSourceUUID()=" + getSourceUUID() + ", getUUID()="
				+ getUUID() + "]";
	}

}
