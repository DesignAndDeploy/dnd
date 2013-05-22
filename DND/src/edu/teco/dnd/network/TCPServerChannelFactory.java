package edu.teco.dnd.network;

import io.netty.bootstrap.ChannelFactory;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelStateHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

class TCPServerChannelFactory {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(TCPServerChannelFactory.class);
	
	private final ChannelFactory<? extends ServerSocketChannel> parentFactory;
	
	private final EventLoopGroup eventLoopGroup;
	
	private final ChannelHandler childHandler;
	
	TCPServerChannelFactory(final ChannelFactory<? extends ServerSocketChannel> parentFactory,
			final EventLoopGroup eventLoopGroup, final ChannelHandler childHandler) {
		LOGGER.entry(parentFactory, eventLoopGroup);
		this.parentFactory = parentFactory;
		this.eventLoopGroup = eventLoopGroup;
		this.childHandler = childHandler;
		LOGGER.exit();
	}

	public ChannelFuture bind(final InetSocketAddress address) {
		LOGGER.entry(address);
		final ServerSocketChannel channel = parentFactory.newChannel();
		
		channel.pipeline().addLast(new TCPServerAcceptor());
		
		final ChannelPromise regPromise = channel.newPromise();
		LOGGER.debug("registering channel {} with promise {}", channel, regPromise);
		eventLoopGroup.register(channel, regPromise);
		if (regPromise.cause() != null) {
			LOGGER.warn("could not register channel {}, {}", channel, regPromise);
			if (channel.isRegistered()) {
				channel.close();
			} else {
				channel.unsafe().closeForcibly();
			}
			LOGGER.exit(regPromise);
			return regPromise;
		}
		
		final ChannelPromise promise = channel.newPromise();
		if (regPromise.isDone()) {
			LOGGER.debug("doing bind now");
			doBind(regPromise, channel, address, promise);
		} else {
			LOGGER.debug("doing bind later");
			regPromise.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(final ChannelFuture future) throws Exception {
					doBind(future, channel, address, promise);
				}
			});
		}
		
		return promise;
	}
	
	private void doBind(final ChannelFuture regFuture, final ServerSocketChannel channel,
			final InetSocketAddress address, final ChannelPromise promise) {
		channel.eventLoop().execute(new Runnable() {
			@Override
			public void run() {
				if (regFuture.isSuccess()) {
					channel.bind(address, promise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
				} else {
					promise.setFailure(regFuture.cause());
				}
			}
		});
	}
	
	private class TCPServerAcceptor extends ChannelStateHandlerAdapter
		implements ChannelInboundMessageHandler<Channel> {
		@Override
		public void inboundBufferUpdated(final ChannelHandlerContext ctx) {
			ThreadContext.put("serverAddress", ctx.channel().localAddress().toString());
			LOGGER.entry(ctx);
			final MessageBuf<Channel> in = ctx.inboundMessageBuffer();
			for (;;) {
				final Channel child = in.poll();
				if (child == null) {
					break;
				}
				
				child.pipeline().addLast(childHandler);
				
				try {
					eventLoopGroup.register(child);
				} catch (final Throwable t) {
					child.unsafe().closeForcibly();
					LOGGER.catching(Level.WARN, t);
				}
			}
			LOGGER.exit();
			ThreadContext.remove("serverAddress");
		}
		
		@Override
		public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
			LOGGER.entry(ctx, cause);
			final ChannelConfig config = ctx.channel().config();
			if (config.isAutoRead()) {
				// See https://github.com/netty/netty/issues/1328
				config.setAutoRead(false);
				ctx.channel().eventLoop().schedule(new Runnable() {
					@Override
					public void run() {
						config.setAutoRead(true);
					}
				}, 1, TimeUnit.SECONDS);
			}
			ctx.fireExceptionCaught(cause);
			LOGGER.exit();
		}

		@Override
		public MessageBuf<Channel> newInboundBuffer(final ChannelHandlerContext ctx) {
			return Unpooled.messageBuffer();
		}
	}
}
