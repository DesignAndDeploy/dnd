package edu.teco.dnd.network.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.ConnectionListener;
import edu.teco.dnd.network.DelegatingConnectionListener;

public class ClientChannelManager implements RemoteUUIDResolver {
	private static final Logger LOGGER = LogManager.getLogger(ClientChannelManager.class);
	
	private static final AttributeKey<UUID> REMOTE_UUID_KEY = new AttributeKey<UUID>("remote UUID");
	private static final AttributeKey<Boolean> ACTIVE_KEY = new AttributeKey<Boolean>("active");

	private final Set<Channel> channels = new HashSet<Channel>();
	private final Map<UUID, Set<Channel>> channelsByRemoteUUID = new HashMap<UUID, Set<Channel>>();

	private final DelegatingConnectionListener delegatingConnectionListener = new DelegatingConnectionListener();
	
	private final ClientChannelFactory clientChannelFactory;
	
	public ClientChannelManager(final ClientChannelFactory clientChannelFactory) {
		this.clientChannelFactory = clientChannelFactory;
	}
	
	public ChannelFuture connect(final SocketAddress address) {
		return clientChannelFactory.connect(address);
	}

	public void addChannel(final Channel channel) {
		LOGGER.debug("adding channel {}", channel);
		synchronized (channels) {
			channels.add(channel);
			channel.closeFuture().addListener(new ChannelRemover());
		}
	}

	public Collection<Channel> getChannels() {
		synchronized (channels) {
			return new HashSet<Channel>(channels);
		}
	}

	public Set<Channel> getChannels(final UUID remoteUUID) {
		synchronized (channels) {
			final Set<Channel> channels = channelsByRemoteUUID.get(remoteUUID);
			if (channels == null) {
				return Collections.emptySet();
			} else {
				return new HashSet<Channel>(channels);
			}
		}
	}

	private void removeChannel(final Channel channel) {
		assert channel != null;
		synchronized (channels) {
			if (isActive(channel)) {
				informConnectionClosedIfLast(channel);
			}

			channels.remove(channel);
		}
	}

	private void informConnectionClosedIfLast(final Channel channel) {
		synchronized (channels) {
			final UUID remoteUUID = getRemoteUUID(channel);
			final Collection<Channel> activeChannels = getActiveChannels(remoteUUID);

			if (activeChannels.size() <= 1) {
				delegatingConnectionListener.connectionClosed(remoteUUID);
			}
		}
	}

	private Collection<Channel> getActiveChannels(final UUID remoteUUID) {
		synchronized (channels) {
			final Collection<Channel> activeChannels = new ArrayList<Channel>();
			for (final Channel channel : getChannels(remoteUUID)) {
				if (isActive(channel)) {
					activeChannels.add(channel);
				}
			}
			return activeChannels;
		}
	}

	private class ChannelRemover implements ChannelFutureListener {
		@Override
		public void operationComplete(final ChannelFuture future) {
			assert future != null;
			removeChannel(future.channel());
		}
	}

	public boolean setActiveIfFirst(final Channel channel) {
		final UUID remoteUUID;
		synchronized (channels) {
			remoteUUID = getRemoteUUID(channel);
			if (remoteUUID == null) {
				throw new IllegalArgumentException("channel " + channel + " does not have an UUID");
			}

			final Collection<Channel> channels = getChannels(remoteUUID);
			for (final Channel otherChannel : channels) {
				if (channel.equals(otherChannel)) {
					continue;
				}

				if (isActive(otherChannel)) {
					return false;
				}
			}

			setActive(channel);
		}

		delegatingConnectionListener.connectionEstablished(remoteUUID);

		return true;
	}

	public void setRemoteUUID(final Channel channel, final UUID remoteUUID) {
		synchronized (channels) {
			if (!channels.contains(channel)) {
				throw new IllegalArgumentException("channel " + channel + " was not added to this manager");
			}
			removeRemoteUUID(channel);
			if (remoteUUID != null) {
				addRemoteUUID(channel, remoteUUID);
			}
		}
	}

	private void removeRemoteUUID(final Channel channel) {
		final Attribute<UUID> remoteUUIDAttribute = channel.attr(REMOTE_UUID_KEY);
		final UUID oldUUID = remoteUUIDAttribute.get();
		remoteUUIDAttribute.remove();

		if (oldUUID != null) {
			LOGGER.debug("removing UUID {} from {}", oldUUID, channel);
			Set<Channel> channelsWithRemoteUUID = channelsByRemoteUUID.get(oldUUID);
			channelsWithRemoteUUID.remove(channel);
			if (channelsWithRemoteUUID.isEmpty()) {
				channelsByRemoteUUID.remove(oldUUID);
			}
		}
	}

	private void addRemoteUUID(final Channel channel, final UUID remoteUUID) {
		LOGGER.debug("setting UUID {} for {}", remoteUUID, channel);
		final Attribute<UUID> remoteUUIDAttribute = channel.attr(REMOTE_UUID_KEY);
		remoteUUIDAttribute.set(remoteUUID);

		Set<Channel> channelsWithRemoteUUID = channelsByRemoteUUID.get(remoteUUID);
		if (channelsWithRemoteUUID == null) {
			channelsWithRemoteUUID = new HashSet<Channel>();
			channelsByRemoteUUID.put(remoteUUID, channelsWithRemoteUUID);
		}

		channelsWithRemoteUUID.add(channel);
	}

	@Override
	public UUID getRemoteUUID(final Channel channel) {
		final Attribute<UUID> remoteUUIDAttribute = channel.attr(REMOTE_UUID_KEY);
		return remoteUUIDAttribute.get();
	}

	public boolean isActive(final Channel channel) {
		final Attribute<Boolean> activeAttribute = channel.attr(ACTIVE_KEY);
		final Boolean value = activeAttribute.get();
		if (value == null) {
			return false;
		}
		return value;
	}

	public void setActive(final Channel channel) {
		synchronized (channels) {
			if (!channels.contains(channel)) {
				throw new IllegalArgumentException("channel " + channel + " was not added to this manager");
			}
			
			final UUID remoteUUID = getRemoteUUID(channel);
			final boolean informListeners = (remoteUUID != null && getActiveChannels(remoteUUID).isEmpty());

			final Attribute<Boolean> activeAttribute = channel.attr(ACTIVE_KEY);
			LOGGER.debug("setting active attribute on {}", channel);
			activeAttribute.set(true);
			
			if (informListeners) {
				delegatingConnectionListener.connectionEstablished(remoteUUID);
			}
		}
	}

	public void addConnectionListener(final ConnectionListener listener) {
		delegatingConnectionListener.addListener(listener);
	}

	public void removeConnectionListener(final ConnectionListener listener) {
		delegatingConnectionListener.removeListener(listener);
	}
}
