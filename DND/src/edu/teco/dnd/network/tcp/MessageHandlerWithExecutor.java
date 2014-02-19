package edu.teco.dnd.network.tcp;

import java.util.concurrent.Executor;

import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;

/**
 * Combines a MessageHandler with an Executor to make it easier to pass both around at the same time.
 * 
 * @param <T>
 *            type of Messages handled by the MessageHandler
 */
public interface MessageHandlerWithExecutor<T extends Message> {
	public abstract MessageHandler<T> getMessageHandler();

	public abstract Executor getExecutor();
}