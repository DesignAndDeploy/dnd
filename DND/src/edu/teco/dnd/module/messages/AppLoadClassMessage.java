package edu.teco.dnd.module.messages;

import edu.teco.dnd.network.messages.Message;

/**
 * contains the bytecode of a class to be loaded.
 */
public class AppLoadClassMessage implements Message{
	
	public static final String MESSAGE_TYPE = "appLoadClass";
	
	String className;
	byte[] classByteCode;
}
