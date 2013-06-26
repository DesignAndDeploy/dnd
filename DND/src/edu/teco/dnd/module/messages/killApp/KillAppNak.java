package edu.teco.dnd.module.messages.killApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

/**
 * send when a new Application is supposed to be started.
 * @author Marvin Marx
 *
 */
public class KillAppNak extends Message {
	
	
	public static final String MESSAGE_TYPE = "startAppNak";
	
	public UUID appId;
	
	public KillAppNak(UUID appId) {
		this.appId = appId;
	}
	
	public KillAppNak(KillAppMessage msg) {
		this.appId = msg.appId;
	}
	
	@SuppressWarnings("unused")/*for gson*/
	private KillAppNak() {
		appId = null;
	}
}
