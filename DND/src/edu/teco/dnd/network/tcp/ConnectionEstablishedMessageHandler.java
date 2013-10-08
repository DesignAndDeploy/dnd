package edu.teco.dnd.network.tcp;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.messages.ConnectionEstablishedMessage;

@Sharable
public class ConnectionEstablishedMessageHandler extends SimpleChannelInboundHandler<ConnectionEstablishedMessage> {
	private static final Logger LOGGER = LogManager.getLogger(ConnectionEstablishedMessageHandler.class);

	private final ClientChannelManager clientChannelManager;
	private final UUID localUUID;

	public ConnectionEstablishedMessageHandler(final ClientChannelManager clientChannelManager, final UUID localUUID) {
		this.clientChannelManager = clientChannelManager;
		this.localUUID = localUUID;
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
			
			final UUID storedRemoteUUID = getChannelRemoteUUID(ctx);
			final UUID messageRemoteUUID = msg.getRemoteUUID();
			if (storedRemoteUUID != null && !storedRemoteUUID.equals(messageRemoteUUID)) {
				LOGGER.warn("got ConnectionEstablishedMessage with UUID {}, but UUID {} is already stored for this "
						+ "connection, closing", messageRemoteUUID, storedRemoteUUID);
				ctx.close();
				LOGGER.exit();
				return;
			}
			
			if (messageRemoteUUID == null) {
				LOGGER.warn("got ConnectionEtablishedMessage with UUID null, closing");
				ctx.close();
				LOGGER.exit();
				return;
			}
			
			if (localUUID.equals(messageRemoteUUID)) {
				LOGGER.warn("got ConnectionEstablishedMessage with my own UUID, closing");
				ctx.close();
				LOGGER.exit();
				return;
			}
			
			if (isMasterFor(messageRemoteUUID)) {
				LOGGER.warn("got ConnectionEstablishedMessage from a slave, closing");
				ctx.close();
				LOGGER.exit();
				return;
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("setting channel {} to {} active", ctx.channel(), messageRemoteUUID);
			}
			clientChannelManager.setRemoteUUID(ctx.channel(), messageRemoteUUID);
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

	private UUID getChannelRemoteUUID(final ChannelHandlerContext ctx) {
		return clientChannelManager.getRemoteUUID(ctx.channel());
	}

	private boolean isMasterFor(final UUID remoteUUID) {
		return localUUID.compareTo(remoteUUID) < 0;
	}
}
