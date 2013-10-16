package edu.teco.dnd.network.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.teco.dnd.util.FutureNotifier;
import edu.teco.dnd.util.JoinedFutureNotifier;

/**
 * Allows to bind to addresses and close all open sockets.
 * 
 * This class is thread-safe.
 * 
 * @author Philipp Adolf
 */
public class ServerChannelManager {
	private final Set<Channel> serverChannels = new HashSet<Channel>();
	
	private final ServerChannelFactory channelFactory;
	
	public ServerChannelManager(final ServerChannelFactory channelFactory) {
		this.channelFactory = channelFactory;
	}
	
	/**
	 * Adds a Channel to the list of known Channels.
	 * 
	 * @param serverChannel the Channel to add
	 */
	private void addChannel(final Channel serverChannel) {
		synchronized (this) {
			serverChannels.add(serverChannel);
		}
		serverChannel.closeFuture().addListener(new CloseFutureListener());
	}
	
	/**
	 * Removes a Channel from the list of known Channels.
	 * 
	 * @param serverChannel the Channel to remove
	 */
	private void removeChannel(final Channel serverChannel) {
		synchronized (this) {
			serverChannels.remove(serverChannel);
		}
	}
	
	/**
	 * Used to remove Channels from this manager once they're closed.
	 * 
	 * @author Philipp Adolf
	 */
	private class CloseFutureListener implements ChannelFutureListener {
		@Override
		public void operationComplete(final ChannelFuture future) throws Exception {
			removeChannel(future.channel());
		}
	}

	/**
	 * Binds to a new address. The channel is automatically added to this manager.
	 * 
	 * @param listenAddress the address to bind to
	 * @return a ChannelFutureNotifier that will return the Channel
	 */
	public ChannelFutureNotifier bind(final SocketAddress listenAddress) {
		if (listenAddress == null) {
			throw new IllegalArgumentException("listenAddress must not be null");
		}
		final ChannelFutureNotifier channelFuture = channelFactory.bind(listenAddress);
		addChannel(channelFuture.channel());
		return channelFuture;
	}

	/**
	 * Closes all currently known Channels.
	 * 
	 * @return a FutureNotifier that can be used wait for all Channels to be closed
	 */
	public FutureNotifier<Collection<Void>> closeAllChannels() {
		final Collection<Channel> openChannels = new ArrayList<Channel>();
		synchronized (this) {
			openChannels.addAll(serverChannels);
		}
		
		final Collection<FutureNotifier<? extends Void>> closeFutureNotifiers =
				new ArrayList<FutureNotifier<? extends Void>>(openChannels.size());
		for (final Channel channel : openChannels) {
			final ChannelFuture closeFuture = channel.close();
			closeFutureNotifiers.add(new ChannelFutureNotifierWrapper(closeFuture));
		}

		return new JoinedFutureNotifier<Void>(closeFutureNotifiers);
	}

	/**
	 * Returns all currently known Channels.
	 * 
	 * @return all currently known Channels
	 */
	public Collection<Channel> getChannels() {
		synchronized (this) {
			return new HashSet<Channel>(serverChannels);
		}
	}
}
