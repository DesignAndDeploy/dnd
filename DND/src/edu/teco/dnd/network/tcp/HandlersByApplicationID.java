package edu.teco.dnd.network.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;

/**
 * <p>
 * Manages a set {@link MessageHandler}s for a single Message class.
 * </p>
 * 
 * <p>
 * A default MessageHandler can be registered as well as handlers for specific Application IDs. When queried with an
 * Application ID this class will either return the handler registered for that ID if it exists or the default handler
 * otherwise
 * </p>
 * 
 * @author Philipp Adolf
 * 
 * @param <T>
 *            the Message class this class will be used for
 */
// TODO: add methods to remove handlers
public class HandlersByApplicationID<T extends Message> {
	private final Map<UUID, MessageHandler<? super T>> handlers = new HashMap<UUID, MessageHandler<? super T>>();

	/**
	 * Sets the default handler.
	 * 
	 * This handler will be used if no Application specific handler can be found.
	 * 
	 * @param handler
	 *            the handler that will be the used if no Application specific handler can be found
	 */
	public void setDefaultHandler(final MessageHandler<? super T> handler) {
		setHandler(ConnectionManager.APPID_DEFAULT, handler);
	}

	/**
	 * Sets an application specific handler.
	 * 
	 * @param applicationID
	 *            the ID of the Application the handler should be used for
	 * @param handler
	 *            the handler to use for that Application
	 */
	public void setHandler(final UUID applicationID, final MessageHandler<? super T> handler) {
		if (applicationID == null) {
			throw new IllegalArgumentException("applicationID must not be null");
		}
		if (handler == null) {
			throw new IllegalArgumentException("handler must not be null");
		}
		handlers.put(applicationID, handler);
	}

	/**
	 * Returns the handler that should be used for the given Application.
	 * 
	 * If a handler was registered for the given Application ID that handler is returned, otherwise the default handler
	 * is returned.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @return the handler that should be used for that Application. May be null.
	 */
	public MessageHandler<? super T> getHandler(final UUID applicationID) {
		final MessageHandler<? super T> applicationSpecificHandler = getApplicationSpecificHandler(applicationID);
		if (applicationSpecificHandler == null) {
			return getDefaultHandler();
		}
		return applicationSpecificHandler;
	}

	/**
	 * Returns the default handler.
	 * 
	 * @return the default handler. May be null.
	 */
	public MessageHandler<? super T> getDefaultHandler() {
		return getApplicationSpecificHandler(ConnectionManager.APPID_DEFAULT);
	}

	/**
	 * Returns the handler that was registered for the given Application ID.
	 * 
	 * @param applicationID
	 *            the Application ID to check
	 * @return the handler for the given Application ID. May be null.
	 */
	public MessageHandler<? super T> getApplicationSpecificHandler(final UUID applicationID) {
		return handlers.get(applicationID);
	}
}