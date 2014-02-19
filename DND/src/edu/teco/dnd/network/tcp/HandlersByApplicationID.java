package edu.teco.dnd.network.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;

/**
 * <p>
 * Manages a set {@link MessageHandler}s for a single {@link Message} class.
 * </p>
 * 
 * <p>
 * A default MessageHandler can be registered as well as handlers for specific {@link ApplicationID}s. When queried with
 * an ApplicationID this class will either return the handler registered for that ID if it exists or the default handler
 * otherwise
 * </p>
 * 
 * @param <T>
 *            the Message class this class will be used for
 */
// TODO: add methods to remove handlers
public class HandlersByApplicationID<T extends Message> {
	private final Map<ApplicationID, MessageHandlerWithExecutor<? super T>> handlersWithExecutors =
			new HashMap<ApplicationID, MessageHandlerWithExecutor<? super T>>();

	/**
	 * Sets the default handler with the default {@link Executor}.
	 * 
	 * This handler will be used if no {@link Application} specific handler can be found.
	 * 
	 * @param handler
	 *            the handler that will be the used if no Application specific handler can be found
	 */
	public void setDefaultHandler(final MessageHandler<? super T> handler) {
		setDefaultHandler(handler, null);
	}

	/**
	 * Sets the default handler with the given {@link Executor}.
	 * 
	 * @param handler
	 *            the handler that will be used if no {@link Application} specific handler can be found
	 * @param executor
	 *            the Executor to use for the given handler. Pass <code>null</code> to use the default Executor.
	 */
	public void setDefaultHandler(final MessageHandler<? super T> handler, final Executor executor) {
		setHandler(ConnectionManager.APPID_DEFAULT, handler, executor);
	}

	/**
	 * Sets an {@link Application} specific handler with the default executor.
	 * 
	 * @param applicationID
	 *            the ID of the Application the handler should be used for
	 * @param handler
	 *            the handler to use for that Application
	 */
	public void setHandler(final ApplicationID applicationID, final MessageHandler<? super T> handler) {
		setHandler(applicationID, handler, null);
	}

	/**
	 * Sets an {@link Application} specific handler with a given executor.
	 * 
	 * @param applicationID
	 *            the ID of the Application the handler should be used for
	 * @param handler
	 *            the handler to use for that Application
	 * @param executor
	 *            the Executor to use for the handler. Pass <code>null</code> to use the default Executor.
	 */
	@SuppressWarnings("unchecked")
	public void setHandler(final ApplicationID applicationID, final MessageHandler<? super T> handler,
			final Executor executor) {
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
	 * Returns the handler that should be used for the given {@link Application} together with its {@link Executor}
	 * (which may be <code>null</code>).
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @return the handler that should be used for that Application. May be <code>null</code>.
	 */
	public MessageHandlerWithExecutor<T> getHandlerWithExecutor(final ApplicationID applicationID) {
		final MessageHandlerWithExecutor<T> applicationSpecifcHandler =
				getApplicationSpecificHandlerWithExecutor(applicationID);
		if (applicationSpecifcHandler == null) {
			return getDefaultHandlerWithExecutor();
		}
		return applicationSpecifcHandler;
	}

	/**
	 * Returns the default handler.
	 * 
	 * @return the default handler. May be <code>null</code>.
	 */
	public MessageHandlerWithExecutor<T> getDefaultHandlerWithExecutor() {
		return getApplicationSpecificHandlerWithExecutor(ConnectionManager.APPID_DEFAULT);
	}

	/**
	 * Returns the handler that was registered for the given {@link ApplicationID}.
	 * 
	 * @param applicationID
	 *            the ApplicationID to check
	 * @return the handler for the given ApplicationID. May be <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public MessageHandlerWithExecutor<T> getApplicationSpecificHandlerWithExecutor(final ApplicationID applicationID) {
		return (MessageHandlerWithExecutor<T>) handlersWithExecutors.get(applicationID);
	}

	/**
	 * Returns the handler that should be used for the given {@link Application}.
	 * 
	 * If a handler was registered for the given {@link ApplicationID} that handler is returned, otherwise the default
	 * handler is returned.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @return the handler that should be used for that Application. May be <code>null</code>.
	 * @deprecated Use {@link #getHandlerWithExecutor(ApplicationID)} instead.
	 */
	@Deprecated
	public MessageHandler<T> getHandler(final ApplicationID applicationID) {
		final MessageHandler<T> applicationSpecificHandler = getApplicationSpecificHandler(applicationID);
		if (applicationSpecificHandler == null) {
			return getDefaultHandler();
		}
		return applicationSpecificHandler;
	}

	/**
	 * Returns the default handler.
	 * 
	 * @return the default handler. May be <code>null</code>.
	 * @deprecated Use {@link #getDefaultHandlerWithExecutor()} instead.
	 */
	@Deprecated
	public MessageHandler<T> getDefaultHandler() {
		return getApplicationSpecificHandler(ConnectionManager.APPID_DEFAULT);
	}

	/**
	 * Returns the handler that was registered for the given {@link ApplicationID}.
	 * 
	 * @param applicationID
	 *            the Application ID to check
	 * @return the handler for the given Application ID. May be <code>null</code>.
	 * @deprecated Use {@link #getApplicationSpecificHandlerWithExecutor(ApplicationID)} instead.
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public MessageHandler<T> getApplicationSpecificHandler(final ApplicationID applicationID) {
		final MessageHandlerWithExecutor<T> handlerWithExecutor =
				(MessageHandlerWithExecutor<T>) handlersWithExecutors.get(applicationID);
		if (handlerWithExecutor == null) {
			return null;
		}
		return handlerWithExecutor.getMessageHandler();
	}
}