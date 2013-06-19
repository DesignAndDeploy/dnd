package edu.teco.dnd.module.messages.values;

import java.io.Serializable;
import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;


public class AppValueMessage extends ApplicationSpecificMessage {
	public final UUID functionBlock;
	public final String input;
	public final Serializable value;
	
	public AppValueMessage(UUID appId, UUID functionBlock,  String input, Serializable value) {
		super(appId);
		this.functionBlock = functionBlock;
		this.input = input;
		this.value = value;
	}
}
