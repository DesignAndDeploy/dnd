package edu.teco.dnd.network;

import java.util.List;
import java.util.UUID;

/**
 * An implementation of ConnectionManager that uses TCP connections for communication.
 * 
 * @author Philipp Adolf
 */
public class TCPConnectionManager implements ConnectionManager, BeaconListener {
	// TODO: finish skeleton
	
	@Override
	public void beaconFound(BeaconMessage beacon) {
	}

	@Override
	public void sendMessage(UUID uuid, Message message) {
	}

	@Override
	public void addHandler(UUID appid, MessageHandler handler) {
	}

	@Override
	public void addHandler(MessageHandler handler) {
	}

	@Override
	public List<UUID> getConnectedModules() {
		return null;
	}
}
