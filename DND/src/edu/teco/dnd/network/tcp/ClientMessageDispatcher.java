package edu.teco.dnd.network.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;
import edu.teco.dnd.network.messages.DefaultResponse;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.messages.Response;

/**
 * <p>
 * Dispatches incoming {@link Message}s to registered {@link MessageHandler}s.
 * </p>
 * 
 * <p>
 * When a Message is received this class checks the registered MessageHandlers for the best match and calls that
 * handler. It then sends a Response - either the one the handler returned or a {@link DefaultResponse} otherwise. A
 * DefaultResponse is also sent if no handler was found.
 * </p>
 * 
 * <p>
 * To find a handler this class first tries to see if there is a handler registered for the exact class of the Message.
 * If there is no such handler the search starts again with the superclass of the Message. If the Message is an
 * {@link ApplicationSpecificMessage} the basic algorithm is the same, but for every class it is first checked if there
 * as a handler registered for the Application ID of the ApplicationSpecificMessage and then the default handler is
 * checked. Only if both are unset the superclass is used.
 * </p>
 * 
 * <p>
 * If a Response is received a {@link ResponseFutureManager} is informed about it and no handler is called.
 * </p>
 * 
 * @author Philipp Adolf
 */
@Sharable
public class ClientMessageDispatcher extends SimpleChannelInboundHandler<Message> {
	private static final Logger LOGGER = LogManager.getLogger(ClientMessageDispatcher.class);

	private final MessageHandlerManager handlers = new MessageHandlerManager();

	private final RemoteIDResolver remoteIDResolver;
	private final ResponseFutureManager responseFutureManager;

	public ClientMessageDispatcher(final RemoteIDResolver remoteIDResolver,
			final ResponseFutureManager responseFutureManager) {
		this.remoteIDResolver = remoteIDResolver;
		this.responseFutureManager = responseFutureManager;
	}

	/**
	 * Sets the default handler for the given Message class.
	 * 
	 * This handler will be used if the Message does not have an Application ID (for example because it is not an
	 * {@link ApplicationSpecificMessage}) or if no handler was registered for the Message class and Application ID.
	 * 
	 * @param messageClass
	 *            the class to register a default handler for
	 * @param handler
	 *            the default handler to register
	 */
	public <T extends Message> void setDefaultHandler(final Class<T> messageClass,
			final MessageHandler<? super T> handler) {
		LOGGER.debug("setting default handler for {} to {}", messageClass, handler);
		handlers.setDefaultHandler(messageClass, handler);
	}

	/**
	 * Sets the default handler and its Executor for the given Message class.
	 * 
	 * This handler will be used if the Message does not have an Application ID (for example because it is not an
	 * {@link ApplicationSpecificMessage}) or if no handler was registered for the Message class and Application ID.
	 * 
	 * @param messageClass
	 *            the class to register a default handler for
	 * @param handler
	 *            the default handler to register
	 * @param executor
	 *            the Executor that should be used to execute the MessageHandler
	 */
	public <T extends Message> void setDefaultHandler(final Class<T> messageClass,
			final MessageHandler<? super T> handler, final Executor executor) {
		LOGGER.debug("setting default handler for {} to {} with executor {}", messageClass, handler, executor);
		handlers.setDefaultHandler(messageClass, handler, executor);
	}

	/**
	 * Sets the handler to use for {@link ApplicationSpecificMessage}s of a given type and with a given Application ID.
	 * 
	 * This can also be called with Messages that are not ApplicationSpecificMessages, however the handler will never be
	 * used as those Messages do not provide an Application ID.
	 * 
	 * @param messageClass
	 *            the class of Messages the handler should be registered for
	 * @param handler
	 *            the handler to register
	 * @param applicationID
	 *            the Application ID the handler should be registered for
	 */
	public <T extends Message> void setHandler(final Class<T> messageClass, final MessageHandler<? super T> handler,
			final ApplicationID applicationID) {
		LOGGER.debug("setting handler for {} with application ID {} to {}", messageClass, applicationID, handler);
		handlers.setHandler(messageClass, handler, applicationID);
	}

	/**
	 * Sets the handler and its Executor to use for {@link ApplicationSpecificMessage}s of a given type and with a given
	 * Application ID.
	 * 
	 * This can also be called with Messages that are not ApplicationSpecificMessages, however the handler will never be
	 * used as those Messages do not provide an Application ID.
	 * 
	 * @param messageClass
	 *            the class of Messages the handler should be registered for
	 * @param handler
	 *            the handler to register
	 * @param applicationID
	 *            the Application ID the handler should be registered for
	 * @param executor
	 *            the Executor that should be used to execute the MessageHandler
	 */
	public <T extends Message> void setHandler(final Class<T> messageClass, final MessageHandler<? super T> handler,
			final ApplicationID applicationID, final Executor executor) {
		LOGGER.debug("setting handler for {} with application ID {} to {} with executor {}", messageClass,
				applicationID, handler, executor);
		handlers.setHandler(messageClass, handler, applicationID, executor);
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Message msg) {
		LOGGER.entry(ctx, msg);
		if (msg instanceof Response) {
			responseFutureManager.setSuccess((Response) msg);
			return;
		}

		MessageHandlerWithExecutor<Message> handlerWithExecutor;

		try {
			handlerWithExecutor = getHandlerWithExecutor(msg);
		} catch (final NoSuchElementException e) {
			sendDefaultResponse(ctx.channel(), msg.getUUID());
			return;
		}

		handlerWithExecutor.getExecutor().execute(
				new Dispatcher(msg, handlerWithExecutor.getMessageHandler(), ctx.channel()));
		LOGGER.exit();
	}

	@SuppressWarnings("unchecked")
	private <T extends Message> MessageHandlerWithExecutor<T> getHandlerWithExecutor(final T message) {
		if (message instanceof ApplicationSpecificMessage) {
			return (MessageHandlerWithExecutor<T>) handlers.getHandler(message.getClass(),
					((ApplicationSpecificMessage) message).getApplicationID());
		} else {
			return (MessageHandlerWithExecutor<T>) handlers.getDefaultHandler(message.getClass());
		}
	}

	private void sendDefaultResponse(final Channel c, final UUID sourceUUID) {
		sendResponse(c, new DefaultResponse(), sourceUUID);
	}

	private void sendResponse(final Channel c, final Response response, final UUID sourceUUID) {
		response.setSourceUUID(sourceUUID);
		LOGGER.debug("sending Response {}", response);
		c.writeAndFlush(response);
	}

	private class Dispatcher implements Runnable {
		private final Message message;
		private final MessageHandler<Message> messageHandler;
		private final Channel channel;

		private Dispatcher(final Message message, final MessageHandler<Message> messageHandler, Channel channel) {
			this.message = message;
			this.messageHandler = messageHandler;
			this.channel = channel;
		}

		@Override
		public void run() {
			LOGGER.entry(message, messageHandler, channel);
			Response response = null;
			try {
				response = messageHandler.handleMessage(remoteIDResolver.getRemoteID(channel), message);
			} catch (final Throwable t) {
				LOGGER.catching(t);
			}
			if (response == null) {
				sendDefaultResponse(channel, message.getUUID());
			} else {
				sendResponse(channel, response, message.getUUID());
			}
			LOGGER.exit();
		}
	}
}
