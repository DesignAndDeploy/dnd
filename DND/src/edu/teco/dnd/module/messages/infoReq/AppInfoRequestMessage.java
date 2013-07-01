package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class AppInfoRequestMessage extends ApplicationSpecificMessage {
	public static final String MESSAGE_TYPE = "request application info";

	public AppInfoRequestMessage(final UUID applicationUUID) {
		super(applicationUUID);
	}
}
