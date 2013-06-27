package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

public class ValueAck extends Response {

	public static final String MESSAGE_TYPE = "value ack";

	public ValueAck(UUID appId) {

	}

}
