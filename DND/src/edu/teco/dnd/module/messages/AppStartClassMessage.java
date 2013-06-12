package edu.teco.dnd.module.messages;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * contains the bytecode of a class to be loaded.
 */
public class AppStartClassMessage implements ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "appStartClass";

	public String className;
	public UUID appId;

	public AppStartClassMessage(String className, UUID appId) {
		this.className = className;
		this.appId = appId;
	}

	@Override
	public UUID getApplicationID() {
		return appId;
	}
}
