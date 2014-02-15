package edu.teco.dnd.network.tcp;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.messages.ConnectionEstablishedMessage;

/**
 * <p>
 * Handles incoming {@link ConnectionEstablishedMessage}s.
 * </p>
 * 
 * <p>
 * The handler does some sanity checks for the remote ModuleID sent with the message and if they are passed marks the
 * Channel as active.
 * </p>
 * 
 * @author Philipp Adolf
 */
@Sharable
public class ConnectionEstablishedMessageHandler extends SimpleChannelInboundHandler<ConnectionEstablishedMessage> {
	private static final Logger LOGGER = LogManager.getLogger(ConnectionEstablishedMessageHandler.class);

	private final ClientChannelManager clientChannelManager;
	private final ModuleID localID;

	public ConnectionEstablishedMessageHandler(final ClientChannelManager clientChannelManager, final ModuleID localID) {
		this.clientChannelManager = clientChannelManager;
		this.localID = localID;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final ConnectionEstablishedMessage msg) {
		synchronized (ctx.channel()) {
			LOGGER.entry(ctx, msg);
			if (isActive(ctx)) {
				LOGGER.warn("got ConnectionEstablishedMessage for an active channel, closing");
				ctx.close();
				LOGGER.exit();
				return;
			}

			final ModuleID storedRemoteID = getChannelRemoteID(ctx);
			final ModuleID messageRemoteID = msg.getRemoteID();
			if (storedRemoteID != null && !storedRemoteID.equals(messageRemoteID)) {
				LOGGER.warn("got ConnectionEstablishedMessage with ModuleID {}, but ModuleID {} is already stored for "
						+ "this connection, closing", messageRemoteID, storedRemoteID);
				ctx.close();
				LOGGER.exit();
				return;
			}

			if (messageRemoteID == null) {
				LOGGER.warn("got ConnectionEtablishedMessage with ModuleID null, closing");
				ctx.close();
				LOGGER.exit();
				return;
			}

			if (localID.equals(messageRemoteID)) {
				LOGGER.warn("got ConnectionEstablishedMessage with my own ModuleID, closing");
				ctx.close();
				LOGGER.exit();
				return;
			}

			if (localID.isMasterFor(messageRemoteID)) {
				LOGGER.warn("got ConnectionEstablishedMessage from a slave, closing");
				ctx.close();
				LOGGER.exit();
				return;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("setting channel {} to {} active", ctx.channel(), messageRemoteID);
			}
			clientChannelManager.setRemoteID(ctx.channel(), messageRemoteID);
			setActive(ctx);
			LOGGER.exit();
		}
	}

	private boolean isActive(final ChannelHandlerContext ctx) {
		return clientChannelManager.isActive(ctx.channel());
	}

	private void setActive(final ChannelHandlerContext ctx) {
		clientChannelManager.setActive(ctx.channel());
	}

	private ModuleID getChannelRemoteID(final ChannelHandlerContext ctx) {
		return clientChannelManager.getRemoteID(ctx.channel());
	}
}
