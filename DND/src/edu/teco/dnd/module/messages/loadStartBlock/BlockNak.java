package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

public class BlockNak extends Response {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "block nak";

	public String className;

	public BlockNak(String className, UUID appId) {
		this.className = className;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private BlockNak() {
		className = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((className == null) ? 0 : className.hashCode());
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
		BlockNak other = (BlockNak) obj;
		if (className == null) {
			if (other.className != null) {
				return false;
			}
		} else if (!className.equals(other.className)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BlockNak [className=" + className + ", getSourceUUID()=" + getSourceUUID() + ", getUUID()=" + getUUID()
				+ "]";
	}

	
}
