package edu.teco.dnd.network;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class TCPClientChannelFactory {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(TCPClientChannelFactory.class);
	
	private final ChannelFactory<? extends Channel> parentFactory;
	
	private final EventLoopGroup eventLoopGroup;
	
	private final ChannelHandler handler;
	
	TCPClientChannelFactory(final ChannelFactory<? extends Channel> parentFactory, final EventLoopGroup eventLoopGroup,
			final ChannelHandler handler) {
		LOGGER.entry(parentFactory);
		this.parentFactory = parentFactory;
		this.eventLoopGroup = eventLoopGroup;
		this.handler = handler;
		LOGGER.exit();
	}
	
	public ChannelFuture connect(final InetSocketAddress address) {
		LOGGER.entry(address);
		final Channel channel = parentFactory.newChannel();
		
		channel.pipeline().addLast(handler);
		
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
			return regPromise;
		}
		
		final ChannelPromise promise = channel.newPromise();
		if (regPromise.isDone()) {
			LOGGER.debug("doing connect now");
			doConnect(regPromise, channel, address, promise);
		} else {
			LOGGER.debug("doing connect later");
			regPromise.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(final ChannelFuture future) {
					doConnect(future, channel, address, promise);
				}
			});
		}
		
		LOGGER.exit();
		return promise;
	}
	
	private void doConnect(final ChannelFuture regFuture, final Channel channel,
			final InetSocketAddress address, final ChannelPromise promise) {
		channel.eventLoop().execute(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(regFuture, channel, address, promise);
				if (regFuture.isSuccess()) {
					LOGGER.debug("trying to connect {} to {} with promise {}", channel, address, promise);
					channel.connect(address, promise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
				} else {
					LOGGER.warn("creating child channel failed: {}", regFuture);
					promise.setFailure(regFuture.cause());
				}
				LOGGER.exit();
			}
		});
	}
}
