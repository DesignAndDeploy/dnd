package edu.teco.dnd.network.messages;

/**
 * A message to signal that the sender has closed the connection.
 * 
 * @author Philipp Adolf
 */
public class ConnectionClosedMessage extends Message {
	/**
	 * The type for this message.
	 */
	public static final String MESSAGE_TYPE = "connection closed";

	@Override
	public String toString() {
		return "ConnectionClosedMessage[]";
	}
}
