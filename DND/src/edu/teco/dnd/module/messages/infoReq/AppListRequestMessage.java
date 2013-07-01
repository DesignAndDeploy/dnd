package edu.teco.dnd.module.messages.infoReq;

import edu.teco.dnd.network.messages.Message;

/**
 * Requests a Collection of the Application IDs of all running applications.
 *
 * @author Philipp Adolf
 */
public class AppListRequestMessage extends Message {
	public static final String MESSAGE_TYPE = "application list request";
}
