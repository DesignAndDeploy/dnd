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

public class ServerChannelManager {
	private final Set<Channel> serverChannels = new HashSet<Channel>();
	
	private final ServerChannelFactory channelFactory;
	
	public ServerChannelManager(final ServerChannelFactory channelFactory) {
		this.channelFactory = channelFactory;
	}
	
	private void addChannel(final Channel serverChannel) {
		synchronized (this) {
			serverChannels.add(serverChannel);
		}
		serverChannel.closeFuture().addListener(new CloseFutureListener());
	}
	
	private void removeChannel(final Channel serverChannel) {
		synchronized (this) {
			serverChannels.remove(serverChannel);
		}
	}
	
	private class CloseFutureListener implements ChannelFutureListener {
		@Override
		public void operationComplete(final ChannelFuture future) throws Exception {
			removeChannel(future.channel());
		}
	}

	public ChannelFutureNotifier bind(final SocketAddress listenAddress) {
		if (listenAddress == null) {
			throw new IllegalArgumentException("listenAddress must not be null");
		}
		final ChannelFutureNotifier channelFuture = channelFactory.bind(listenAddress);
		addChannel(channelFuture.channel());
		return channelFuture;
	}

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

	public Collection<Channel> getChannels() {
		synchronized (this) {
			return new HashSet<Channel>(serverChannels);
		}
	}
}
