package edu.teco.dnd.network;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

/**
 * A listener that will be informed about new messages.
 * 
 * @author Philipp Adolf
 */
public interface MessageHandler<T extends Message> {
	/**
	 * This method is called if a new message is received. This method may be called from multiple Threads
	 * simultaneously.
	 * 
	 * @param connectionManager
	 *            connectionManager calling this method.
	 * @param remtoeUUID
	 *            the UUID of the module that sent the message
	 * @param message
	 *            the message that was received
	 */
	void handleMessage(ConnectionManager connectionManager, UUID remoteUUID, T message);
}
