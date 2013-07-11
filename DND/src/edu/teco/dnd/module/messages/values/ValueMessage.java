package edu.teco.dnd.module.messages.values;

import java.io.Serializable;
import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class ValueMessage extends ApplicationSpecificMessage {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "value";
	public final UUID blockId;
	public final String input;
	public final Serializable value;

	public ValueMessage(UUID appId, UUID functionBlock, String input, Serializable value) {
		super(appId);
		this.blockId = functionBlock;
		this.input = input;
		this.value = value;
	}
}
