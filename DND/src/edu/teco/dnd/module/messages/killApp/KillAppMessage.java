package edu.teco.dnd.module.messages.killApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

/**
 * send when a new Application is supposed to be stopped.
 * @author cryptkiddy
 *
 */
public class KillAppMessage extends Message {
	
	
	public static final String MESSAGE_TYPE = "killApp";
	
	public UUID appId;
	
	public KillAppMessage(UUID appId) {
		this.appId = appId;
	}
	
	@SuppressWarnings("unused")/*for gson*/
	private KillAppMessage() {
		appId = null;
	}
}
