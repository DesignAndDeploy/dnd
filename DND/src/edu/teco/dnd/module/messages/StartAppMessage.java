package edu.teco.dnd.module.messages;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

/**
 * send when a new Application is supposed to be started.
 * @author cryptkiddy
 *
 */
public class StartAppMessage implements Message {
	
	
	public static final String MESSAGE_TYPE = "startApp";
	
	public String name;
	public UUID appId;
	
	public StartAppMessage(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}
	
	@SuppressWarnings("unused")/*for gson*/
	private StartAppMessage() {
		name = null;
		appId = null;
	}
}
