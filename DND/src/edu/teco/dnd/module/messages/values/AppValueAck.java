package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class AppValueAck extends ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "appValueAck";

	public AppValueAck(UUID appId) {
		super(appId);
	}
}
