package edu.teco.dnd.network.messages;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleID;

/**
 * Message that signals that the sender has accepted the connection. Also contains the {@link ModuleID} of the sending
 * {@link Module}.
 */
public class ConnectionEstablishedMessage extends Message {
	public static final String MESSAGE_TYPE = "connection established";
	
	private final ModuleID remoteID;
	
	public ConnectionEstablishedMessage(final ModuleID remoteID) {
		super();
		this.remoteID = remoteID;
	}

	@Override
	public String toString() {
		return "ConnectionEstablishedMessage[uuid=" + getUUID() + ",remoteID=" + remoteID + "]";
	}

	public ModuleID getRemoteID() {
		return remoteID;
	}
}
