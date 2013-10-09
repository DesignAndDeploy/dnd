package edu.teco.dnd.network.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;

public class ServerBootstrapChannelFactory implements ServerChannelFactory {
	private final ServerBootstrap bootstrap;

	public ServerBootstrapChannelFactory(final ServerBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	@Override
	public synchronized ChannelFutureNotifierWrapper bind(final SocketAddress address) {
		return new ChannelFutureNotifierWrapper(bootstrap.bind(address));
	}

	@Override
	public synchronized void setChildChannelInitializer(ChannelHandler initializer) {
		bootstrap.childHandler(initializer);
	}
}
