package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class AppValueNak implements ApplicationSpecificMessage {
	public enum ErrorType {
		WRONG_MODULE, // TODO need a way to handle if not even the app is running on the module.
		INVALID_INPUT, OTHER;
	}

	public static final String MESSAGE_TYPE = "app value nak";
	private final UUID appId;
	public final ErrorType errorType;
	public final UUID funcBlockId;
	public final String inputId;

	public AppValueNak(UUID appId, ErrorType errorType, UUID funcBlockId, String inputId) {
		if (errorType == null) {
			errorType = ErrorType.OTHER;
		}
		this.appId = appId;
		this.errorType = errorType;
		this.funcBlockId = funcBlockId;
		this.inputId = inputId;
	}

	@Override
	public UUID getApplicationID() {
		return appId;
	}

}
