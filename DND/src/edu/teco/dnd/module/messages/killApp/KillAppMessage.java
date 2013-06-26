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
	
	
	public KillAppMessage(UUID appId) {
		super(appId);
	}
	
	/*for gson*/
	private KillAppMessage() {
		super(null);
	}
}
