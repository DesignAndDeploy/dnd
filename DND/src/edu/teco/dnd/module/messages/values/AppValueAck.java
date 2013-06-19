package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class AppValueAck implements ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "appValueAck";

	public final UUID appId;

	public AppValueAck(UUID appId) {
		this.appId = appId;
	}

	@Override
	public UUID getApplicationID() {
		return appId;
	}

}
