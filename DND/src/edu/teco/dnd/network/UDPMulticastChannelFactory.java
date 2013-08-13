package edu.teco.dnd.network;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: replace with subclass of AbstractBootstrap
public class UDPMulticastChannelFactory {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(UDPMulticastChannelFactory.class);

	/**
	 * The parent factory.
	 */
	private final ChannelFactory<? extends DatagramChannel> parentFactory;

	private final EventLoopGroup eventLoopGroup;

	/**
	 * The ChannelHandler for new channels.
	 */
	private final ChannelHandler handler;

	/**
	 * Initializes a new UDPMulticastChannelFactory.
	 * 
	 * @param parentFactory
	 *            the parent factory
	 * @param eventLoopGroup
	 *            the EventLoopGroup to use
	 * @param handler
	 *            the ChannelHandler for new channels
	 */
	public UDPMulticastChannelFactory(final ChannelFactory<? extends DatagramChannel> parentFactory,
			final EventLoopGroup eventLoopGroup, final ChannelHandler handler) {
		LOGGER.entry(parentFactory, eventLoopGroup, handler);
		this.parentFactory = parentFactory;
		this.eventLoopGroup = eventLoopGroup;
		this.handler = handler;
		LOGGER.exit();
	}

	/**
	 * Binds to a multicast address on a given interface.
	 * 
	 * @param interf
	 *            the interface to use
	 * @param address
	 *            a multicast address. Must be resolved.
	 */
	@SuppressWarnings("unchecked")
	public ChannelFuture bind(final NetworkInterface interf, final InetSocketAddress address,
			final Map<AttributeKey<?>, Object> attrs) {
		LOGGER.entry(interf, address);
		if (!address.getAddress().isMulticastAddress()) {
			throw new IllegalArgumentException("address is not a multicast address");
		}

		final DatagramChannel channel = parentFactory.newChannel();

		for (final Entry<AttributeKey<?>, Object> attr : attrs.entrySet()) {
			channel.attr((AttributeKey<Object>) attr.getKey()).set(attr.getValue());
		}

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

		final ChannelPromise bindPromise = channel.newPromise();
		final ChannelPromise joinPromise = channel.newPromise();
		if (regPromise.isDone()) {
			LOGGER.debug("doing bind now");
			doBind(regPromise, channel, interf, address, bindPromise, joinPromise);
		} else {
			LOGGER.debug("doing bind later");
			regPromise.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(final ChannelFuture future) throws Exception {
					doBind(future, channel, interf, address, bindPromise, joinPromise);
				}
			});
		}

		LOGGER.exit(joinPromise);
		return joinPromise;
	}

	private void doBind(final ChannelFuture regFuture, final DatagramChannel channel, final NetworkInterface interf,
			final InetSocketAddress address, final ChannelPromise bindPromise, final ChannelPromise joinPromise) {
		channel.eventLoop().execute(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(regFuture, channel, address, bindPromise, joinPromise);
				if (regFuture.isSuccess()) {
					LOGGER.debug("trying to bind {} to {} with promise {}", channel, address, bindPromise);
					channel.config().setNetworkInterface(interf);
					channel.config().setReuseAddress(true);
					channel.bind(new InetSocketAddress(address.getPort()), bindPromise);
				} else {
					bindPromise.setFailure(regFuture.cause());
				}
				if (bindPromise.isDone()) {
					LOGGER.debug("doing join now");
					doJoin(bindPromise, channel, interf, address, joinPromise);
				} else {
					LOGGER.debug("doing join later");
					bindPromise.addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(final ChannelFuture future) throws Exception {
							doJoin(bindPromise, channel, interf, address, joinPromise);
						}
					});
				}
				LOGGER.exit();
			}
		});
	}

	private void doJoin(final ChannelFuture bindFuture, final DatagramChannel channel, final NetworkInterface interf,
			final InetSocketAddress address, final ChannelPromise promise) {
		channel.eventLoop().execute(new Runnable() {
			@Override
			public void run() {
				LOGGER.entry(bindFuture, channel, interf, address, promise);
				if (bindFuture.isSuccess()) {
					LOGGER.debug("joining group {} with {} using {} and promise {}", address, interf, channel, promise);
					channel.joinGroup(address, interf, promise);
				} else {
					promise.setFailure(bindFuture.cause());
				}
				LOGGER.exit();
			}
		});
	}
}
