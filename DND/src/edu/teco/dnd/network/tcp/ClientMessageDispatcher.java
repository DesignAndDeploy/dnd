package edu.teco.dnd.network.tcp;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;
import edu.teco.dnd.network.messages.DefaultResponse;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.messages.Response;

@Sharable
public class ClientMessageDispatcher extends SimpleChannelInboundHandler<Message> {
	private static final Logger LOGGER = LogManager.getLogger(ClientMessageDispatcher.class);

	private final MessageHandlerManager handlers = new MessageHandlerManager();

	private final RemoteUUIDResolver remoteUUIDResolver;
	private final ResponseFutureManager responseFutureManager;

	public ClientMessageDispatcher(final RemoteUUIDResolver remoteUUIDResolver, final ResponseFutureManager responseFutureManager) {
		this.remoteUUIDResolver = remoteUUIDResolver;
		this.responseFutureManager = responseFutureManager;
	}

	public <T extends Message> void setDefaultHandler(final Class<T> messageClass,
			final MessageHandler<? super T> handler) {
		LOGGER.debug("setting normal default handler for {} to {}", messageClass, handler);
		handlers.setDefaultHandler(messageClass, handler);
	}

	public <T extends Message> void setHandler(final Class<T> messageClass, final MessageHandler<? super T> handler,
			final UUID applicationID) {
		LOGGER.debug("setting normal handler for {} with application ID {} to {}", messageClass, applicationID, handler);
		handlers.setHandler(messageClass, handler, applicationID);
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Message msg) {
		LOGGER.entry(ctx, msg);
		if (msg instanceof Response) {
			responseFutureManager.setSuccess((Response) msg);
			return;
		}

		final UUID remoteUUID = remoteUUIDResolver.getRemoteUUID(ctx.channel());
		Response response = null;

		try {
			response = callHandler(remoteUUID, msg);
		} catch (final NoSuchElementException e) {
			LOGGER.catching(Level.WARN, e);
		} catch (final InvocationTargetException e) {
			LOGGER.catching(Level.WARN, e);
		}

		if (response == null) {
			sendDefaultResponse(ctx, msg.getUUID());
		} else {
			sendResponse(ctx, response, msg.getUUID());
		}
		LOGGER.exit();
	}

	private Response callHandler(final UUID remoteUUID, final Message msg) throws InvocationTargetException {
		MessageHandler<? super Message> handler = getHandler(msg);
		try {
			return handler.handleMessage(remoteUUID, msg);
		} catch (final Throwable t) {
			throw new InvocationTargetException(t);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Message> MessageHandler<? super T> getHandler(final T message) {
		if (message instanceof ApplicationSpecificMessage) {
			return (MessageHandler<? super T>) handlers.getHandler(message.getClass(),
					((ApplicationSpecificMessage) message).getApplicationID());
		} else {
			return (MessageHandler<? super T>) handlers.getDefaultHandler(message.getClass());
		}
	}

	private void sendDefaultResponse(final ChannelHandlerContext ctx, final UUID sourceUUID) {
		sendResponse(ctx, new DefaultResponse(), sourceUUID);
	}

	private void sendResponse(final ChannelHandlerContext ctx, final Response response, final UUID sourceUUID) {
		response.setSourceUUID(sourceUUID);
		LOGGER.debug("sending Response {}", response);
		ctx.writeAndFlush(response);
	}
}
