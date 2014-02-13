package edu.teco.dnd.module.messages.loadStartBlock;

import edu.teco.dnd.network.messages.Response;

/**
 * Send when loading the Class bytecode into the application failed.
 * 
 * @author Marvin Marx
 * 
 */
public class LoadClassNak extends Response {

	public static final String MESSAGE_TYPE = "load class nak";

	/**
	 * name of the class we wanted to load.
	 */
	public String className;

	/**
	 * 
	 * @param className
	 *            name of the class we wanted to load.
	 */
	public LoadClassNak(String className) {
		this.className = className;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private LoadClassNak() {
		className = null;
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
		return "LoadClassNak [className=" + className + ", getSourceUUID()=" + getSourceUUID() + ", getUUID()="
				+ getUUID() + "]";
	}

}
