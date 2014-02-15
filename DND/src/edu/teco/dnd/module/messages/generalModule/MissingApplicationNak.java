package edu.teco.dnd.module.messages.generalModule;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.Response;

/**
 * Message being send if a message was received for an application that does not reside on this module.
 * 
 * @author Marvin Marx
 * 
 */
public class MissingApplicationNak extends Response {

	/**
	 * Message Type.
	 */
	public static final String MESSAGE_TYPE = "missing app nak";

	/**
	 * ID of the Application that the original message was for.
	 */
	public ApplicationID applicationID;

	/**
	 * @param applicationID
	 *            ID of the Application that the original message was for.
	 */
	public MissingApplicationNak(ApplicationID applicationID) {
		this.applicationID = applicationID;
	}

	public MissingApplicationNak() {
		this(null);
	}
}
