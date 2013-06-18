package edu.teco.dnd.module.messages.killApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

/**
 * send when a new Application is supposed to be started.
 *
 */
public class KillAppAck implements Message {
	
	
	public static final String MESSAGE_TYPE = "killAppAck";
	
	public UUID appId;
	
	public KillAppAck(UUID appId) {
		this.appId = appId;
	}
	
	public KillAppAck(KillAppMessage msg) {
		this.appId = msg.appId;
	}
	
	@SuppressWarnings("unused")/*for gson*/
	private KillAppAck() {
		appId = null;
	}
}
