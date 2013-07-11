package edu.teco.dnd.module.messages.infoReq;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.network.messages.Response;

public class ModuleInfoMessage extends Response {
	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "module info";
	public final Module module;

	public ModuleInfoMessage(final ConfigReader conf) {
		module = new Module(conf.getUuid(), conf.getName(), conf.getBlockRoot());
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
