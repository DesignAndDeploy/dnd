package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.network.messages.Response;

public class ModuleInfoMessage extends Response {

	public static String MESSAGE_TYPE = "module info";
	public final Module module;
	
	public ModuleInfoMessage(final UUID sourceUUID, final UUID uuid, final Module module) {
		super(sourceUUID, uuid);
		this.module = module;
	}

	public ModuleInfoMessage(final Module module) {
		this.module = module;
	}

	public Module getModule() {
		return this.module;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((module == null) ? 0 : module.hashCode());
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ModuleInfoMessage [module=" + module + ", getSourceUUID()=" + getSourceUUID() + ", getUUID()="
				+ getUUID() + "]";
	}
	
	
}
