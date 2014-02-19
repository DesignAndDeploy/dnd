package edu.teco.dnd.network.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;

import java.net.SocketAddress;

/**
 * An implementation of {@link ServerChannelFactory} that uses {@link ServerBootstrap}.
 * 
 * <p>
 * The {@link EventLoopGroup} and the {@link Channel} type of the Bootstrap object have to be set:
 * 
 * <pre>
 * final ServerBootstrap b = new ServerBootstrap();
 * b.group(networkGroup, applicationGroup);
 * b.channel(NioServerSocketChannel.class);
 * ServerBootstrapChannelFactory factory = new ServerBootstrapChannelFactory(b);
 * </pre>
 * 
 * </p>
 */
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
