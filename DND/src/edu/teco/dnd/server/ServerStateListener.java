package edu.teco.dnd.server;

import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;

/**
 * Gets informed if the server components are started or stopped.
 * 
 * @author Philipp Adolf
 */
public interface ServerStateListener {
	void serverStarted(ConnectionManager connectionManager, UDPMulticastBeacon beacon);

	void serverStopped();
}
