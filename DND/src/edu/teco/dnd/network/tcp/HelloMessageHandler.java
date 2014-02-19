package edu.teco.dnd.network.tcp;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.messages.ConnectionEstablishedMessage;
import edu.teco.dnd.network.messages.HelloMessage;
import edu.teco.dnd.network.messages.Message;

/**
 * Handles incoming {@link HelloMessage}s. This will set the {@link ModuleID remote ID} in the
 * {@link ClientChannelManager} and, if this machine is the {@link ModuleID#isMasterFor(ModuleID) master}, determine
 * whether or not the connection will be kept. Appropriate {@link Message}s will be sent.
 */
@Sharable
public class HelloMessageHandler extends SimpleChannelInboundHandler<HelloMessage> {
	private static Logger LOGGER = LogManager.getLogger(HelloMessageHandler.class);

	private final ClientChannelManager clientChannelManager;
	private final ModuleID localID;

	public HelloMessageHandler(final ClientChannelManager clientChannelManager, final ModuleID localID) {
		this.clientChannelManager = clientChannelManager;
		this.localID = localID;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final HelloMessage msg) {
		synchronized (ctx.channel()) {
			LOGGER.entry(ctx, msg);
			if (moreThanOneHelloMessage(ctx)) {
				LOGGER.warn("got more than one HelloMessage from {}/{}", getRemoteID(ctx), getRemoteAddress(ctx));
			}
			final ModuleID remoteID = msg.getModuleID();
			if (remoteID == null) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("got a HelloMessage with module ID null from {}, disconnecting", getRemoteAddress(ctx));
				}
				ctx.close();
				LOGGER.exit();
				return;
			}
			if (localID.equals(remoteID)) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(
							"got a connection from myself/a ModuleInfo with the same ID ({} from {}, disconnecting",
							localID, getRemoteAddress(ctx));
				}
				ctx.close();
				LOGGER.exit();
				return;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("setting remote ID of {} to {}", ctx.channel(), remoteID);
			}
			clientChannelManager.setRemoteID(ctx.channel(), remoteID);

			if (localID.isMasterFor(remoteID)) {
				if (clientChannelManager.setActiveIfFirst(ctx.channel())) {
					LOGGER.debug("sending connection established to {}", remoteID);
					sendConnectionEstablished(ctx);
				} else {
					LOGGER.debug("connection with {} already established, closing", remoteID);
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
		return getRemoteID(ctx) != null;
	}

	private Object getRemoteID(final ChannelHandlerContext ctx) {
		return clientChannelManager.getRemoteID(ctx.channel());
	}

	private void sendConnectionEstablished(final ChannelHandlerContext context) {
		context.writeAndFlush(new ConnectionEstablishedMessage(localID));
	}
}
