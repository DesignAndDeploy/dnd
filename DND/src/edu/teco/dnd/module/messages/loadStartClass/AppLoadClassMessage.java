package edu.teco.dnd.module.messages.loadStartClass;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * contains the bytecode of a class to be loaded.
 */
public class AppLoadClassMessage extends ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "appLoadClass";

	public String className;
	public byte[] classByteCode;

	public AppLoadClassMessage(String className, byte[] classByteCode, UUID appId) {
		super(appId);
		this.className = className;
		this.classByteCode = classByteCode;
	}
}
