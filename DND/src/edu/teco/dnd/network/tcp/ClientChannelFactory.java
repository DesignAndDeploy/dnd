package edu.teco.dnd.network.tcp;

import java.net.SocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

/**
 * A factory for Channels that can be used to connect to other clients.
 */
public interface ClientChannelFactory {
	/**
	 * Connects to the given address.
	 * 
	 * @param address
	 *            the address to connect to
	 * @return a {@link ChannelFuture} that will return a {@link Channel} connected to <code>address</code>
	 */
	ChannelFuture connect(SocketAddress address);

	/**
	 * Sets a {@link ChannelHandler} that will be executed for every new {@link Channel}.
	 * 
	 * @param handler
	 *            the ChannelHandler to execute for new Channels
	 */
	void setChannelInitializer(final ChannelHandler handler);
}
