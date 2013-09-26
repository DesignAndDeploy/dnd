package edu.teco.dnd.network.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;

// TODO: add methods to remove handlers
public class HandlersByApplicationID<T extends Message> {
	private final Map<UUID, MessageHandler<? super T>> handlers = new HashMap<UUID, MessageHandler<? super T>>();
	
	public void setDefaultHandler(final MessageHandler<? super T> handler) {
		setHandler(ConnectionManager.APPID_DEFAULT, handler);
	}
	
	public void setHandler(final UUID applicationID, final MessageHandler<? super T> handler) {
		if (applicationID == null) {
			throw new IllegalArgumentException("applicationID must not be null");
		}
		if (handler == null) {
			throw new IllegalArgumentException("handler must not be null");
		}
		handlers.put(applicationID, handler);
	}
	
	public MessageHandler<? super T> getHandler(final UUID applicationID) {
		final MessageHandler<? super T> applicationSpecificHandler = getApplicationSpecificHandler(applicationID);
		if (applicationSpecificHandler == null) {
			return getDefaultHandler();
		}
		return applicationSpecificHandler;
	}
	
	public MessageHandler<? super T> getDefaultHandler() {
		return getApplicationSpecificHandler(ConnectionManager.APPID_DEFAULT);
	}
	
	public MessageHandler<? super T> getApplicationSpecificHandler(final UUID applicationID) {
		return handlers.get(applicationID);
	}
}