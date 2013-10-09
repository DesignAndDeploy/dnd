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
	ChannelFutureNotifier bind(SocketAddress address);

	void setChildChannelInitializer(ChannelHandler initializer);
}
