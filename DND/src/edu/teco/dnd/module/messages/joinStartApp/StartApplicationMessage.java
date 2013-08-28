package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * send to a module, when it is supposed to Start an application it joined before.
 * 
 * @author Marvin Marx
 * 
 */
public class StartApplicationMessage extends ApplicationSpecificMessage {

	public static String MESSAGE_TYPE = "start application";

	/**
	 * 
	 * @param appId
	 *            UUID of the application to start.
	 */
	public StartApplicationMessage(UUID appId) {
		super(appId);
	}

	@Override
	public String toString() {
		return "StartApplicationMessage [getApplicationID()=" + getApplicationID() + ", getUUID()=" + getUUID() + "]";
	}

}
