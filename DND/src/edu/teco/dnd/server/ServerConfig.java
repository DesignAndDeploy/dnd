package edu.teco.dnd.server;

import edu.teco.dnd.module.ModuleID;

/**
 * A configuration for the servers managed by a {@link ServerManager}.
 */
public interface ServerConfig {
	/**
	 * Returns the {@link ModuleID} the server should use.
	 * 
	 * @return the ModuleID the server should use
	 */
	ModuleID getModuleID();

	/**
	 * Returns the time between two announcements. This is only used if the servers do regular announces of their
	 * presence.
	 * 
	 * @return the time between two announcements
	 */
	int getAnnounceInterval();
}
