package edu.teco.dnd.network;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.messages.Response;

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
	 * @param remtoeUUID
	 *            the UUID of the module that sent the message
	 * @param message
	 *            the message that was received
	 */
	Response handleMessage(UUID remoteUUID, T message);
}
