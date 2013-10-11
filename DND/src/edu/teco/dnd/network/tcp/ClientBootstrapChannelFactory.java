package edu.teco.dnd.network.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;

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
