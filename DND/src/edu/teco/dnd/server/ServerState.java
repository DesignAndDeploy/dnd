package edu.teco.dnd.server;

/**
 * The state of the server. It will always progress from {@link #STOPPED} to {@link #STARTING}, {@link #RUNNING} and
 * finally {@link #STOPPING} from which it goes to {@link #STOPPED} again.
 */
public enum ServerState {
	/**
	 * The server is currently not running and the server state does not change.
	 */
	STOPPED,

	/**
	 * The server was stopped and is now starting.
	 */
	STARTING,

	/**
	 * The server is currently running and the server state does not change.
	 */
	RUNNING,

	/**
	 * The server was running and is now stopping.
	 */
	STOPPING
}
