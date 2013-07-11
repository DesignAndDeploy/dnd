package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class StartApplicationMessage extends ApplicationSpecificMessage {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "start application";

	public StartApplicationMessage(UUID appId) {
		super(appId);
	}
}
