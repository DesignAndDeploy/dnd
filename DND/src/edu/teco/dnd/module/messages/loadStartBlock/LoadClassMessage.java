package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.Arrays;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * Contains bytecode of a class that is supposed to be loaded into an application by a module.
 * 
 * @author Marvin Marx
 * 
 */
public class LoadClassMessage extends ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "load class";

	/**
	 * Name of the class to be loaded.
	 */
	public String className;
	/**
	 * bytecode of the class.
	 */
	public byte[] classByteCode;

	/**
	 * 
	 * @param className
	 *            Name of the class to be loaded.
	 * @param classByteCode
	 *            bytecode of said class
	 * @param applicationID
	 *            id of the application class is supposed to be loaded into.
	 */
	public LoadClassMessage(String className, byte[] classByteCode, ApplicationID applicationID) {
		super(applicationID);
		this.className = className;
		this.classByteCode = classByteCode;
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
		result = prime * result + Arrays.hashCode(classByteCode);
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
		LoadClassMessage other = (LoadClassMessage) obj;
		if (!Arrays.equals(classByteCode, other.classByteCode)) {
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
		return "LoadClassMessage [className=" + className + ", classByteCode=" + Arrays.toString(classByteCode)
				+ ", getApplicationID()=" + getApplicationID() + ", getUUID()=" + getUUID() + "]";
	}

}
