package edu.teco.dnd.network.messages;

/**
 * A message to signal that the sender has closed the connection. When this Message is received the ConnectionManager
 * should close the connection as well.
 */
public class ConnectionClosedMessage extends Message {
	public static final String MESSAGE_TYPE = "connection closed";

	@Override
	public String toString() {
		return "ConnectionClosedMessage[]";
	}
}
