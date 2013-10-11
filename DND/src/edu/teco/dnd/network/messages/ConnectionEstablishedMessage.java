package edu.teco.dnd.network.messages;

import java.util.UUID;

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
	
	private final UUID remoteUUID;
	
	public ConnectionEstablishedMessage(final UUID remoteUUID) {
		super();
		this.remoteUUID = remoteUUID;
	}

	@Override
	public String toString() {
		return "ConnectionEstablishedMessage[uuid=" + getUUID() + ",remoteUUID=" + remoteUUID + "]";
	}

	public UUID getRemoteUUID() {
		return remoteUUID;
	}
}
