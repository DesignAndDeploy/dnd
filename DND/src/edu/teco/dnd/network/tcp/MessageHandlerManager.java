package edu.teco.dnd.network.tcp;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;

public class MessageHandlerManager {
	private final ConcurrentMap<Class<? extends Message>, HandlersByApplicationID<? extends Message>> handlers =
			new ConcurrentHashMap<Class<? extends Message>, HandlersByApplicationID<? extends Message>>();

	public <T extends Message> void setDefaultHandler(final Class<T> messageClass, final MessageHandler<? super T> handler) {
		final HandlersByApplicationID<T> messageClassHandlers = getHandlersForClass(messageClass);
		synchronized (messageClassHandlers) {
			messageClassHandlers.setDefaultHandler(handler);
		}
	}

	public <T extends Message> void setHandler(final Class<T> messageClass, final MessageHandler<? super T> handler,
			final UUID applicationID) {
		final HandlersByApplicationID<T> messageClassHandlers = getHandlersForClass(messageClass);
		synchronized (messageClassHandlers) {
			messageClassHandlers.setHandler(applicationID, handler);
		}
	}

	/**
	 * Returns the default handler for a given Message class. The search is recursive, that is if the given class has no
	 * default handler the class' superclass is checked until Message itself is reached. If no default handler can be
	 * found a NoSuchElementException is thrown.
	 * 
	 * @param messageClass
	 *            the Message class to look for
	 * @return the default handler for the given class or the first superclass to have a default handler
	 * @throws NoSuchElementException
	 *             if neither the class nor its superclasses have a default handler
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> MessageHandler<? super T> getDefaultHandler(final Class<T> messageClass) {
		final HandlersByApplicationID<T> messageClassHandlers = getHandlersForClass(messageClass);
		MessageHandler<? super T> handler = null;
		synchronized (messageClassHandlers) {
			handler = messageClassHandlers.getDefaultHandler();	
		}
		if (handler == null) {
			try {
				return (MessageHandler<? super T>) getDefaultHandler(getMessageSuperclass(messageClass));
			} catch (final IllegalArgumentException e) {
				throw new NoSuchElementException("no message handler found");
			}
		} else {
			return handler;
		}
	}

	/**
	 * Returns the handler for a given Message class and Application ID. If there is a MessageHandler registered for the
	 * MessageClass and Application ID that handler is returned. Otherwise the default handler for the Message class is
	 * returned. If both are missing the superclass is tried.
	 * 
	 * @param messageClass
	 *            the Message class to look for
	 * @param applicationID
	 *            the Application ID to look for
	 * @return the handler for the given Message class and Application ID
	 * @throws NoSuchElementException
	 *             if the class and its superclasses have neither an Application specific handler nor a default handler
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> MessageHandler<? super T> getHandler(final Class<T> messageClass,
			final UUID applicationID) {
		final HandlersByApplicationID<T> messageClassHandlers = getHandlersForClass(messageClass);
		MessageHandler<? super T> handler = null;
		synchronized (messageClassHandlers) {
			handler = messageClassHandlers.getHandler(applicationID);
		}
		if (handler == null) {
			try {
				return (MessageHandler<? super T>) getHandler(getMessageSuperclass(messageClass), applicationID);
			} catch (final IllegalArgumentException e) {
				throw new NoSuchElementException("no message handler found");
			}
		} else {
			return handler;
		}
	}

	// In case HandlersByApplicationID are removed in other parts of the code this may lead to NullPointerExceptions
	@SuppressWarnings("unchecked")
	private <T extends Message> HandlersByApplicationID<T> getHandlersForClass(final Class<T> messageClass) {
		HandlersByApplicationID<T> handlersForClass = (HandlersByApplicationID<T>) handlers.get(messageClass);
		if (handlersForClass == null) {
			handlers.putIfAbsent(messageClass, new HandlersByApplicationID<T>());
			handlersForClass = (HandlersByApplicationID<T>) handlers.get(messageClass);
		}
		return handlersForClass;
	}

	/**
	 * Returns the superclass of the given class if the superclass is a Message. Throws an IllegalArgumentException
	 * otherwise.
	 * 
	 * @param messageClass
	 *            the class to check
	 * @return the class' superclass if the superclass is a Message
	 * @throws IllegalArgumentException
	 *             if the superclass of the given class is not a Message
	 */
	@SuppressWarnings("unchecked")
	private static Class<? extends Message> getMessageSuperclass(final Class<? extends Message> messageClass) {
		if (hasMessageSuperclass(messageClass)) {
			return (Class<? extends Message>) messageClass.getSuperclass();
		} else {
			throw new IllegalArgumentException(messageClass + " does not have a superclass that is a Message");
		}
	}

	private static boolean hasMessageSuperclass(final Class<? extends Message> messageClass) {
		return Message.class.isAssignableFrom(messageClass.getSuperclass());
	}
}
