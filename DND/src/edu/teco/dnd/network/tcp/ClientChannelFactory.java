package edu.teco.dnd.network.tcp;

import java.net.SocketAddress;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

/**
 * A factory for Channels that can be used to connect to other clients.
 * 
 * @author Philipp Adolf
 */
public interface ClientChannelFactory {
	ChannelFuture connect(SocketAddress address);

	void setChannelInitializer(final ChannelHandler handler);
}
