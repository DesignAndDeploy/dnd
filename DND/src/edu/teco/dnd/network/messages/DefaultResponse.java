package edu.teco.dnd.network.messages;

import java.util.UUID;

import edu.teco.dnd.network.MessageHandler;

/**
 * This Response is sent if a {@link MessageHandler} does not supply its own Response.
 */
public class DefaultResponse extends Response {
	public static final String MESSAGE_TYPE = "default response";

	public DefaultResponse(final UUID sourceUUID) {
		super(sourceUUID);
	}

	public DefaultResponse() {
		super();
	}

	@Override
	public String toString() {
		return "DefaultResponse[uuid=" + getUUID() + ",sourceUUID=" + getSourceUUID() + "]";
	}
}
