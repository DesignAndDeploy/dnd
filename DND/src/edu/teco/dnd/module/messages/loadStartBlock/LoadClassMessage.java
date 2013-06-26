package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * contains the bytecode of a class to be loaded.
 */

public class LoadClassMessage extends ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "load class";
	
	public String className;
	public byte[] classByteCode;
	

	public LoadClassMessage(String className, byte[] classByteCode, UUID appId) {
		super(appId);
		this.className = className;
		this.classByteCode = classByteCode;
	}
}
