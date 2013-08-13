package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class StartApplicationMessage extends ApplicationSpecificMessage {

	public static String MESSAGE_TYPE = "start application";

	public StartApplicationMessage(UUID appId) {
		super(appId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StartApplicationMessage [getApplicationID()=" + getApplicationID() + ", getUUID()=" + getUUID() + "]";
	}

}
