package edu.teco.dnd.module.messages.values;

import java.io.Serializable;
import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;


public class AppValueMessage implements ApplicationSpecificMessage {
	public final UUID appId;
	public final UUID functionBlock;
	public final String input;
	public final Serializable value;
	
	public AppValueMessage(UUID appId,UUID functionBlock,  String input, Serializable value) {
		this.appId = appId;
		this.functionBlock = functionBlock;
		this.input = input;
		this.value = value;
	}

	@Override
	public UUID getApplicationID() {
		return appId;
	}
	
	

}
