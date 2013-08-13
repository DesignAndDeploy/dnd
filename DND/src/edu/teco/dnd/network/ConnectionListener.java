package edu.teco.dnd.network;

import java.util.UUID;

/**
 * A listener that is informed if new connections are made or if connections are closed.
 * 
 * @author Philipp Adolf
 */
public interface ConnectionListener {
	/**
	 * Is called if a new connection is made.
	 * 
	 * @param uuid
	 *            the UUID of the module that is now connected
	 */
	public void connectionEstablished(UUID uuid);

	/**
	 * Is called if a connection is closed.
	 * 
	 * @param uuid
	 *            the UUID of the module that is no longer connected
	 */
	public void connectionClosed(UUID uuid);
}
