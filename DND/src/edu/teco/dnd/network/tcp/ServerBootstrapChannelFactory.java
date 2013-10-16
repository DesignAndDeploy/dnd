package edu.teco.dnd.network.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;

/**
 * An implementation of ServerChannelFactory that uses {@link ServerBootstrap}.
 * 
 * <p>The EventLoopGroup and the Channel type of the Bootstrap object have to be set:
 * 
 * <pre>
 * final ServerBootstrap b = new ServerBootstrap();
 * b.group(networkGroup, applicationGroup);
 * b.channel(NioServerSocketChannel.class);
 * ServerBootstrapChannelFactory factory = new ServerBootstrapChannelFactory(b);
 * </pre>
 * </p>
 * 
 * @author Philipp Adolf
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
