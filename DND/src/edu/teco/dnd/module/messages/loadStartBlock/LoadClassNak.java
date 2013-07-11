package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

public class LoadClassNak extends Response {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "load class nak";

	public UUID appId;
	public String className;

	public LoadClassNak(String className, UUID appId) {
		this.className = className;
		this.appId = appId;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private LoadClassNak() {
		className = null;
		appId = null;
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
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((className == null) ? 0 : className.hashCode());
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
		LoadClassNak other = (LoadClassNak) obj;
		if (appId == null) {
			if (other.appId != null) {
				return false;
			}
		} else if (!appId.equals(other.appId)) {
			return false;
		}
		if (className == null) {
			if (other.className != null) {
				return false;
			}
		} else if (!className.equals(other.className)) {
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
		return "LoadClassNak [appId=" + appId + ", className=" + className + ", getSourceUUID()=" + getSourceUUID()
				+ ", getUUID()=" + getUUID() + "]";
	}

}
