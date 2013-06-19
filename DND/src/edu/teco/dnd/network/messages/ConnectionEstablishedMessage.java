package edu.teco.dnd.network.messages;


/**
 * Message that signals that the sender has accepted the connection.
 *
 * @author Philipp Adolf
 */
public class ConnectionEstablishedMessage extends Message {
	/**
	 * The type of this message.
	 */
	public static final String MESSAGE_TYPE = "connection established";
	
	@Override
	public String toString() {
		return "ConnectionEstablishedMessage[]";
	}
}
