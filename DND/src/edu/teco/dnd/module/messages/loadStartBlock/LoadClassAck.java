package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

/**
 * confirms that the bytecode of a class was successfully loaded.
 * 
 * @author Marvin Marx
 * 
 */
public class LoadClassAck extends Response {

	public static final String MESSAGE_TYPE = "load class ack";
	/**
	 * ID of the application the code was loaded on.
	 */
	public UUID appId;
	/**
	 * Name of the class that was loaded.
	 */
	public String className;

	/**
	 * 
	 * @param className
	 *            name of the class that was loaded
	 * @param appId
	 *            the appID this class was loaded into.
	 */
	public LoadClassAck(String className, UUID appId) {
		this.className = className;
		this.appId = appId;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private LoadClassAck() {
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
		LoadClassAck other = (LoadClassAck) obj;
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
		return "LoadClassAck [appId=" + appId + ", className=" + className + ", getSourceUUID()=" + getSourceUUID()
				+ ", getUUID()=" + getUUID() + "]";
	}

}
