package edu.teco.dnd.network.tcp;

import java.util.concurrent.Executor;

import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;

/**
 * Always returns an executor that runs the code in the thread calling it.
 * 
 * @author Philipp Adolf
 *
 * @param <T> the type of Messages handled by the MessageHandler
 */
public class MessageHandlerWithDefaultExecutor<T extends Message> implements MessageHandlerWithExecutor<T> {
	private final MessageHandler<T> messageHandler;
	
	private static final Executor defaultExecutor = new CurrentThreadExecutor();
	
	public MessageHandlerWithDefaultExecutor(final MessageHandler<T> messageHandler) {
		this.messageHandler = messageHandler;
	}

	@Override
	public MessageHandler<T> getMessageHandler() {
		return messageHandler;
	}

	@Override
	public Executor getExecutor() {
		return defaultExecutor;
	}
	
	private static class CurrentThreadExecutor implements Executor {
		@Override
		public void execute(final Runnable command) {
			command.run();
		}
	}
}
