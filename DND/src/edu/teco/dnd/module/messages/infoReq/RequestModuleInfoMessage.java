package edu.teco.dnd.module.messages.infoReq;

import edu.teco.dnd.network.messages.Message;

/**
 * Request for a message containing information about the module.
 * 
 * @author Marvin Marx
 * 
 */
public class RequestModuleInfoMessage extends Message {
	public static String MESSAGE_TYPE = "request module info";

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RequestModuleInfoMessage [getUUID()=" + getUUID() + "]";
	}

}
