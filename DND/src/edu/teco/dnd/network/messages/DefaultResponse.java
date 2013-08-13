package edu.teco.dnd.network.messages;

import java.util.UUID;

/**
 * This Response is sent if a handler does not supply its own Response.
 * 
 * @author Philipp Adolf
 */
public class DefaultResponse extends Response {
	/**
	 * The type for this Message.
	 */
	public static final String MESSAGE_TYPE = "default response";

	/**
	 * Initializes a new DefaultResponse.
	 * 
	 * @param sourceUUID
	 *            the UUID of the Message this is a response to
	 */
	public DefaultResponse(final UUID sourceUUID) {
		super(sourceUUID);
	}

	/**
	 * Initializes a new DefaultResponse.
	 */
	public DefaultResponse() {
		super();
	}

	@Override
	public String toString() {
		return "DefaultResponse[uuid=" + getUUID() + ",sourceUUID=" + getSourceUUID() + "]";
	}
}
