package edu.teco.dnd.network.tcp;

import java.net.SocketAddress;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

public interface ClientChannelFactory {
	ChannelFuture connect(SocketAddress address);

	void setChannelInitializer(final ChannelHandler handler);
}
