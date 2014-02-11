package edu.teco.dnd.server;

import java.util.UUID;

/**
 * A configuration for the servers managed by a {@link ServerManager}.
 */
public interface ServerConfig {
	/**
	 * Returns the UUID the server should use.
	 * 
	 * @return the UUID the server should use
	 */
	UUID getModuleUUID();

	/**
	 * Returns the time between two announcements. This is only used if the servers do regular announces of their
	 * presence.
	 * 
	 * @return the time between two announcements
	 */
	int getAnnounceInterval();
}
