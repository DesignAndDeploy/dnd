package edu.teco.dnd.module.messages.infoReq;

import edu.teco.dnd.network.messages.Message;

/**
 * Requests a Collection of the Application IDs of all running applications.
 * 
 * @author Philipp Adolf
 */
public class RequestApplicationListMessage extends Message {
	public static final String MESSAGE_TYPE = "application list request";

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RequestApplicationListMessage [getUUID()=" + getUUID() + "]";
	}

}
