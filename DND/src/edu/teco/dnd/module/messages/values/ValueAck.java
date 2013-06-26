package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;


public class ValueAck extends ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "value ack";

	public ValueAck(UUID appId) {
		super(appId);
	}

}
