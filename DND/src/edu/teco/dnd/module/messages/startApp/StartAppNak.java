package edu.teco.dnd.module.messages.startApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

/**
 * send when a new Application is supposed to be started.
 * @author cryptkiddy
 *
 */
public class StartAppNak implements Message {
	
	
	public static final String MESSAGE_TYPE = "startAppNak";
	
	public String name;
	public UUID appId;
	
	public StartAppNak(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}
	
	public StartAppNak(StartAppMessage msg) {
		this.name = msg.name;
		this.appId = msg.appId;
	}
	
	@SuppressWarnings("unused")/*for gson*/
	private StartAppNak() {
		name = null;
		appId = null;
	}
}
