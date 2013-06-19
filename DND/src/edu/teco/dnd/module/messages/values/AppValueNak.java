package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class AppValueNak implements ApplicationSpecificMessage {
	public enum ErrorType {
		WRONG_MODULE, // TODO need a way to handle if not even the app is running on the module.
		INVALID_INPUT, OTHER;
	}

	public static final String MESSAGE_TYPE = "appValueNak";
	private final UUID appId;
	public final ErrorType errorType;

	public AppValueNak(UUID appId, ErrorType errorType) {
		this.appId = appId;
		this.errorType = errorType;
	}

	@Override
	public UUID getApplicationID() {
		return appId;
	}

}
