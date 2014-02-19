package edu.teco.dnd.network;

import edu.teco.dnd.module.ModuleID;

/**
 * A listener that is informed if new connections are made or if connections are closed.
 */
public interface ConnectionListener {
	/**
	 * Is called if a new connection is made.
	 * 
	 * @param moduleID
	 *            the ID of the module that is now connected
	 */
	public void connectionEstablished(ModuleID moduleID);

	/**
	 * Is called if a connection is closed.
	 * 
	 * @param moduleID
	 *            the ID of the module that is no longer connected
	 */
	public void connectionClosed(ModuleID moduleID);
}
