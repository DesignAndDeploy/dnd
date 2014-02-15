package edu.teco.dnd.module.messages.joinStartApp;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * send to a module, when it is supposed to Start an application it joined before.
 * 
 * @author Marvin Marx
 * 
 */
public class StartApplicationMessage extends ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "start application";

	/**
	 * 
	 * @param applicationID
	 *            UUID of the application to start.
	 */
	public StartApplicationMessage(ApplicationID applicationID) {
		super(applicationID);
	}

	@Override
	public String toString() {
		return "StartApplicationMessage [getApplicationID()=" + getApplicationID() + ", getUUID()=" + getUUID() + "]";
	}

}
