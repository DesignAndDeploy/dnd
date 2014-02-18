package edu.teco.dnd.network;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.messages.DefaultResponse;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.messages.Response;

/**
 * Handles incoming Messages. MessageHandlers can be registered with a {@link ConnectionManager} and will then receive
 * matching Messages.
 */
public interface MessageHandler<T extends Message> {
	/**
	 * This method is called if a new message is received. This method may be called from multiple Threads
	 * simultaneously (for different Messages).
	 * 
	 * @param remoteID
	 *            the ID of the module that sent the message
	 * @param message
	 *            the message that was received
	 * @return a Response for the Message. The source UUID of the Response will be set automatically. If
	 *         <code>null</code> is returned a {@link DefaultResponse} is sent.
	 */
	Response handleMessage(ModuleID remoteID, T message) throws Exception;
}
