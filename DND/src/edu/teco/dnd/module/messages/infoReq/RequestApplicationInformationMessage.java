package edu.teco.dnd.module.messages.infoReq;

import edu.teco.dnd.network.messages.Message;

/**
 * Requests a Collection of ApplicationInformations describing the Applications running on the receiving Module.
 * 
 * @author Philipp Adolf
 */
public class RequestApplicationInformationMessage extends Message {
	public static final String MESSAGE_TYPE = "application information request";

	@Override
	public String toString() {
		return "RequestApplicationInformationMessage[getUUID()=" + getUUID() + "]";
	}
}
