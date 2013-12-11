package edu.teco.dnd.network.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

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
	private final Map<UUID, MessageHandlerWithExecutor<? super T>> handlersWithExecutors =
			new HashMap<UUID, MessageHandlerWithExecutor<? super T>>();

	/**
	 * Sets the default handler with the default Executor.
	 * 
	 * This handler will be used if no Application specific handler can be found.
	 * 
	 * @param handler
	 *            the handler that will be the used if no Application specific handler can be found
	 */
	public void setDefaultHandler(final MessageHandler<? super T> handler) {
		setDefaultHandler(handler, null);
	}
	
	/**
	 * Sets the default handler with the given Executor.
	 * 
	 * @param handler the handler that will be used if no Application specifc handler can be found
	 * @param executor the Executor to use for the given handler
	 */
	public void setDefaultHandler(final MessageHandler<? super T> handler, final Executor executor) {
		setHandler(ConnectionManager.APPID_DEFAULT, handler, executor);
	}

	/**
	 * Sets an application specific handler with the default executor (which runs the code in the calling thread).
	 * 
	 * @param applicationID
	 *            the ID of the Application the handler should be used for
	 * @param handler
	 *            the handler to use for that Application
	 */
	public void setHandler(final UUID applicationID, final MessageHandler<? super T> handler) {
		setHandler(applicationID, handler, null);
	}

	/**
	 * Sets an application specific handler with a given executor.
	 * 
	 * @param applicationID
	 *            the ID of the Application the handler should be used for
	 * @param handler
	 *            the handler to use for that Application
	 * @param executor
	 *            the executor to use for the handler. Pass null to use the default executor (which runs the code in the
	 *            calling thread).
	 */
	@SuppressWarnings("unchecked")
	public void setHandler(final UUID applicationID, final MessageHandler<? super T> handler, final Executor executor) {
		if (applicationID == null) {
			throw new IllegalArgumentException("applicationID must not be null");
		}
		if (handler == null) {
			throw new IllegalArgumentException("handler must not be null");
		}
		MessageHandlerWithExecutor<T> handlerWithExecutor = null;
		if (executor == null) {
			handlerWithExecutor = new MessageHandlerWithDefaultExecutor<T>((MessageHandler<T>) handler);
		} else {
			handlerWithExecutor = new SimpleMesssageHandlerWithExecutor<T>((MessageHandler<T>) handler, executor);
		}
		handlersWithExecutors.put(applicationID, handlerWithExecutor);
	}
	
	/**
	 * Returns the handler that should be used for the given Application together with its Executor.
	 * 
	 * @param applicationID the ID of the Application
	 * @return the handler that should be used for that Application. May be null.
	 */
	public MessageHandlerWithExecutor<T> getHandlerWithExecutor(final UUID applicationID) {
		final MessageHandlerWithExecutor<T> applicationSpecifcHandler = getApplicationSpecificHandlerWithExecutor(applicationID);
		if (applicationSpecifcHandler == null) {
			return getDefaultHandlerWithExecutor();
		}
		return applicationSpecifcHandler;
	}
	
	/**
	 * Returns the default handler.
	 * 
	 * @return the default handler. May be null.
	 */
	public MessageHandlerWithExecutor<T> getDefaultHandlerWithExecutor() {
		return getApplicationSpecificHandlerWithExecutor(ConnectionManager.APPID_DEFAULT);
	}

	/**
	 * Returns the handler that was registered for the given Application ID.
	 * 
	 * @param applicationID the Application ID to check
	 * @return the handler for the given Application ID. May be null.
	 */
	@SuppressWarnings("unchecked")
	public MessageHandlerWithExecutor<T> getApplicationSpecificHandlerWithExecutor(final UUID applicationID) {
		return (MessageHandlerWithExecutor<T>) handlersWithExecutors.get(applicationID);
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
	 * @deprecated Use {@link #getHandlerWithExecutor(UUID)} instead.
	 */
	@Deprecated
	public MessageHandler<T> getHandler(final UUID applicationID) {
		final MessageHandler<T> applicationSpecificHandler = getApplicationSpecificHandler(applicationID);
		if (applicationSpecificHandler == null) {
			return getDefaultHandler();
		}
		return applicationSpecificHandler;
	}

	/**
	 * Returns the default handler.
	 * 
	 * @return the default handler. May be null.
	 * @deprecated Use {@link #getDefaultHandlerWithExecutor()} instead.
	 */
	@Deprecated
	public MessageHandler<T> getDefaultHandler() {
		return getApplicationSpecificHandler(ConnectionManager.APPID_DEFAULT);
	}

	/**
	 * Returns the handler that was registered for the given Application ID.
	 * 
	 * @param applicationID
	 *            the Application ID to check
	 * @return the handler for the given Application ID. May be null.
	 * @deprecated Use {@link #getApplicationSpecificHandlerWithExecutor(UUID)} instead.
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public MessageHandler<T> getApplicationSpecificHandler(final UUID applicationID) {
		final MessageHandlerWithExecutor<T> handlerWithExecutor = (MessageHandlerWithExecutor<T>) handlersWithExecutors.get(applicationID);
		if (handlerWithExecutor == null) {
			return null;
		}
		return handlerWithExecutor.getMessageHandler();
	}
}