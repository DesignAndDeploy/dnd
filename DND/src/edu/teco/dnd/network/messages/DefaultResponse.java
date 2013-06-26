package edu.teco.dnd.network.messages;

import java.util.UUID;

public class DefaultResponse extends Response {
	public static final String MESSAGE_TYPE = "default response";
	
	public DefaultResponse(final UUID sourceUUID) {
		super(sourceUUID);
	}
}
