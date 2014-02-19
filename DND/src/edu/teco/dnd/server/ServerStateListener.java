package edu.teco.dnd.server;

import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;

/**
 * Gets informed when servers are started or stopped.
 */
public interface ServerStateListener {
	/**
	 * This method is called when the state of the server changes. If the server is now {@link ServerState#RUNNING} or
	 * is being shut down ({@link ServerState#STOPPING}) the current ConnectionManager and UDPMulticastBeacon is passed
	 * along. For other states these values are <code>null</code>.
	 * 
	 * @param state
	 *            the new state of the server
	 * @param connectionManager
	 *            the ConnectionManager that has been started (for RUNNING) or is being stopped (for STOPPED)
	 * @param beacon
	 *            the UDPMulticastBeacon that has been started (for RUNNING) or is being stopped (for STOPPED)
	 */
	void serverStateChanged(ServerState state, ConnectionManager connectionManager, UDPMulticastBeacon beacon);
}
