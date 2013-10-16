package edu.teco.dnd.network.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;

/**
 * <p>An implementation of ClientChannelFactory that uses {@link Bootstrap} to create Channels.</p>
 * 
 * <p>The EventLoopGroup and the Channel type of the Bootstrap object have to be set:
 * 
 * <pre>
 * Bootstrap b = new Bootstrap;
 * b.group(new NioEventLoopGroup());
 * b.channel(NioSocketChannel.class);
 * ClientBootstrapChannelFactory factory = new ClientBootstrapChannelFactory(b);
 * </pre>
 * </p>
 * 
 * @author Philipp Adolf
 */
public class ClientBootstrapChannelFactory implements ClientChannelFactory {
	private final Bootstrap bootstrap;
	
	public ClientBootstrapChannelFactory(final Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}
	
	@Override
	public ChannelFuture connect(final SocketAddress address) {
		return bootstrap.connect(address);
	}

	@Override
	public void setChannelInitializer(final ChannelHandler handler) {
		bootstrap.handler(handler);
	}
}
