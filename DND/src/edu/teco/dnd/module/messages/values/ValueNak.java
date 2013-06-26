package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;


public class ValueNak extends ApplicationSpecificMessage {
	public enum ErrorType {
		WRONG_MODULE, // TODO need a way to handle if not even the app is running on the module.
		INVALID_INPUT, OTHER;
	}

	//TODO when sending appValueMsg listen for this and similar response (check [...].value for similar things
	public static final String MESSAGE_TYPE = "value nak";
	public final UUID blockId;
	public final String input;

	public final ErrorType errorType;


	public ValueNak(UUID appId, ErrorType errorType, UUID blockId, String input) {
		super(appId);
		if (errorType == null) {
			errorType = ErrorType.OTHER;
		}
		this.errorType = errorType;
		this.blockId = blockId;
		this.input = input;
	}
}
