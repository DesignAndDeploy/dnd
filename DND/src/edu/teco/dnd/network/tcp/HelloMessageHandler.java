package edu.teco.dnd.network.tcp;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketAddress;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.messages.ConnectionEstablishedMessage;
import edu.teco.dnd.network.messages.HelloMessage;

/**
 * Handles incoming {@link HelloMessage}s. This will set the remote UUID in the {@link ClientChannelManager} and, if
 * this machine is the master, determine whether or not the connection will be kept. Appropriate Messages will be sent.
 * 
 * @author Philipp Adolf
 */
@Sharable
public class HelloMessageHandler extends SimpleChannelInboundHandler<HelloMessage> {
	private static Logger LOGGER = LogManager.getLogger(HelloMessageHandler.class);

	private final ClientChannelManager clientChannelManager;
	private final UUID localUUID;

	public HelloMessageHandler(final ClientChannelManager clientChannelManager, final UUID localUUID) {
		this.clientChannelManager = clientChannelManager;
		this.localUUID = localUUID;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final HelloMessage msg) {
		synchronized (ctx.channel()) {
			LOGGER.entry(ctx, msg);
			if (moreThanOneHelloMessage(ctx)) {
				LOGGER.warn("got more than one HelloMessage from {}/{}", getRemoteUUID(ctx), getRemoteAddress(ctx));
			}
			final UUID remoteUUID = msg.getModuleUUID();
			if (remoteUUID == null) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("got a HelloMessage with module UUID null from {}, disconnecting", getRemoteAddress(ctx));
				}
				ctx.close();
				LOGGER.exit();
				return;
			}
			if (localUUID.equals(remoteUUID)) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("got a connection from myself/a Module with the same UUID from {}, disconnecting",
							getRemoteAddress(ctx));
				}
				ctx.close();
				LOGGER.exit();
				return;
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("setting remote UUID of {} to {}", ctx.channel(), remoteUUID);
			}
			clientChannelManager.setRemoteUUID(ctx.channel(), remoteUUID);

			if (isMasterFor(remoteUUID)) {
				if (clientChannelManager.setActiveIfFirst(ctx.channel())) {
					LOGGER.debug("sending connection established to {}", remoteUUID);
					sendConnectionEstablished(ctx);
				} else {
					LOGGER.debug("connection with {} already established, closing", remoteUUID);
					ctx.close();
				}
			}
			LOGGER.exit();
		}
	}

	private SocketAddress getRemoteAddress(final ChannelHandlerContext ctx) {
		return ctx.channel().remoteAddress();
	}

	private boolean moreThanOneHelloMessage(final ChannelHandlerContext ctx) {
		return getRemoteUUID(ctx) != null;
	}

	private Object getRemoteUUID(final ChannelHandlerContext ctx) {
		return clientChannelManager.getRemoteUUID(ctx.channel());
	}

	private boolean isMasterFor(final UUID remoteUUID) {
		return localUUID.compareTo(remoteUUID) < 0;
	}

	private void sendConnectionEstablished(final ChannelHandlerContext context) {
		context.writeAndFlush(new ConnectionEstablishedMessage(localUUID));
	}
}
