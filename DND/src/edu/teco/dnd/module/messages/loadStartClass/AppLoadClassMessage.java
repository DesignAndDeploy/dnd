package edu.teco.dnd.module.messages.loadStartClass;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * contains the bytecode of a class to be loaded.
 */
public class AppLoadClassMessage implements ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "appLoadClass";

	public String className;
	public byte[] classByteCode;
	public UUID appId;

	public AppLoadClassMessage(String className, byte[] classByteCode, UUID appId) {
		this.className = className;
		this.classByteCode = classByteCode;
		this.appId = appId;
	}

	@Override
	public UUID getApplicationID() {
		return appId;
	}
}
