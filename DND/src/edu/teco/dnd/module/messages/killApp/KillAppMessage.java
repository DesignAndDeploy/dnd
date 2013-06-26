package edu.teco.dnd.module.messages.killApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * send when a new Application is supposed to be stopped.
 * @author Marvin Marx
 *
 */

public class KillAppMessage extends ApplicationSpecificMessage {
	
	
	public static final String MESSAGE_TYPE = "kill";
	
	public UUID appId;
	
	public KillAppMessage(UUID appId) {
		this.appId = appId;
	}
	
	@SuppressWarnings("unused")/*for gson*/
	private KillAppMessage() {
		appId = null;
	}

	@Override
	public UUID getApplicationID() {
		return appId;
	}
}
