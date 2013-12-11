package edu.teco.dnd.network.tcp;

import io.netty.channel.ChannelHandler;

import java.net.SocketAddress;

/**
 * A factory for server sockets. Allows to specify a {@link ChannelHandler} that will be used to initialize connections
 * accepted on the server sockets. Implementations must be thread safe.
 * 
 * @author Philipp Adolf
 */
public interface ServerChannelFactory {
	/**
	 * Create a new Channel that listens on the given address.
	 * 
	 * @param address the address to listen on
	 * @return a FutureNotifier that will return the Channel
	 */
	ChannelFutureNotifier bind(SocketAddress address);

	/**
	 * Sets the initializer that will be called to initialize new Channels.
	 * 
	 * @param initializer the initializer to call for new Channels
	 */
	void setChildChannelInitializer(ChannelHandler initializer);
}
