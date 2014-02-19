package edu.teco.dnd.network.tcp;

import java.util.concurrent.Executor;

import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;

/**
 * Basic implementation of {@link MessageHandlerWithExecutor}.
 * 
 * @param <T>
 *            the class of {@link Message}s handled by the {@link MessageHandler}
 */
public class SimpleMesssageHandlerWithExecutor<T extends Message> implements MessageHandlerWithExecutor<T> {
	private final MessageHandler<T> messageHandler;
	private final Executor executor;

	SimpleMesssageHandlerWithExecutor(final MessageHandler<T> messageHandler, final Executor executor) {
		this.messageHandler = messageHandler;
		this.executor = executor;
	}

	@Override
	public MessageHandler<T> getMessageHandler() {
		return this.messageHandler;
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}
}