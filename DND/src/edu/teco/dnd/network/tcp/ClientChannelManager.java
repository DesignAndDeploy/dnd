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

/**
 * Manages Channels that are connected to other clients.
 * 
 * This includes storing a Set of all Channels, the UUIDs of the clients they are connected to and whether or not the
 * Initialization phase has been completed (which makes them active).
 * 
 * All public methods in this class are thread-safe.
 * 
 * @author Philipp Adolf
 */
public class ClientChannelManager implements RemoteUUIDResolver {
	private static final Logger LOGGER = LogManager.getLogger(ClientChannelManager.class);

	private static final AttributeKey<UUID> REMOTE_UUID_KEY = new AttributeKey<UUID>("remote UUID");
	private static final AttributeKey<Boolean> ACTIVE_KEY = new AttributeKey<Boolean>("active");

	private final Set<Channel> channels = new HashSet<Channel>();
	private final Map<UUID, Set<Channel>> channelsByRemoteUUID = new HashMap<UUID, Set<Channel>>();

	private final DelegatingConnectionListener delegatingConnectionListener = new DelegatingConnectionListener();

	private final ClientChannelFactory clientChannelFactory;

	/**
	 * Initializes a new ClientChannelManager.
	 * 
	 * @param clientChannelFactory
	 *            a factory that will be used by {@link #connect(SocketAddress)} to connect to other clients
	 */
	public ClientChannelManager(final ClientChannelFactory clientChannelFactory) {
		this.clientChannelFactory = clientChannelFactory;
	}

	/**
	 * Tries to connect to the given address.
	 * 
	 * This class assumes that ClientChannelFactory given to {@link #ClientChannelManager(ClientChannelFactory)}
	 * installs a handler that will register the Channel with this manager once the connection is made.
	 * 
	 * @param address
	 *            the address to connect to.
	 * @return a Future that can be used to check if creating a TCP connection was successful
	 * @see ClientChannelInitializer
	 */
	/*
	 * TODO: Fix race condition: If connect is called, then a shutdown is down (which uses channels) before the
	 * connection is made, the new Channel will be added to channels after the other ones have been shut down.
	 */
	public ChannelFuture connect(final SocketAddress address) {
		return clientChannelFactory.connect(address);
	}

	/**
	 * Adds a Channel to this manager.
	 * 
	 * This adds the Channel to the list of known Channels and must be called before using {@link #setActive(Channel)},
	 * {@link #setActiveIfFirst(Channel)} and {@link #setRemoteUUID(Channel, UUID)}.
	 * 
	 * @param channel
	 *            the Channel to add. Must not be null.
	 */
	public void addChannel(final Channel channel) {
		LOGGER.debug("adding channel {}", channel);
		synchronized (channels) {
			channels.add(channel);
			channel.closeFuture().addListener(new ChannelRemover());
		}
	}

	/**
	 * Returns all currently known Channels.
	 * 
	 * @return all currently known Channels
	 */
	public Collection<Channel> getChannels() {
		synchronized (channels) {
			return new HashSet<Channel>(channels);
		}
	}

	/**
	 * Returns all Channels that are connected to a client with the given UUID.
	 * 
	 * This includes both active and non-active Channels.
	 * 
	 * @param remoteUUID
	 *            the UUID to look for
	 * @return all Channels that have the given UUID set
	 */
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

	/**
	 * Sets the Channel to the active state if no other Channel with the same remote UUID is active.
	 * 
	 * If there is another Channel with the same remote UUID that is active, nothing is done and <code>false</code> is
	 * returned. If there is no such Channel the given one is marked active and <code>true</code> is returned. The
	 * Channel itself is ignored in the active check, so if the given Channel is active but all other Channels with the
	 * same UUID are inactive <code>true</code> is returned.
	 * 
	 * @param channel
	 *            the Channel to check
	 * @return true if no other Channel with the same UUID was active, false otherwise
	 * @throws IllegalArgumentException
	 *             if the given Channel does not have remote UUID set or was not added to this manager
	 */
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

	/**
	 * Sets the UUID of the client that is on the other end of the Channel.
	 * 
	 * @param channel the Channel for which the UUID should be set
	 * @param remoteUUID the UUID of the client on the other end of the Channel
	 * @throws IllegalArgumentException if the Channel was not added to this manager
	 */
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
	
	/**
	 * Returns whether or not a Channel is active.
	 * 
	 * @param channel the Channel to check
	 * @return true if the Channel is active, false otherwise
	 */
	public boolean isActive(final Channel channel) {
		final Attribute<Boolean> activeAttribute = channel.attr(ACTIVE_KEY);
		final Boolean value = activeAttribute.get();
		if (value == null) {
			return false;
		}
		return value;
	}

	/**
	 * Marks a Channel as active.
	 * 
	 * @param channel the Channel to mark active
	 * @throws IllegalArgumentException if the Channel was not added to this manager
	 */
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

	/**
	 * Adds a ConnectionListener.
	 * 
	 * The listener will be informed of established Connections (Channels marked as active) as well as closed
	 * connections.
	 * 
	 * @param listener the ConnectionListener to add
	 */
	public void addConnectionListener(final ConnectionListener listener) {
		delegatingConnectionListener.addListener(listener);
	}

	/**
	 * Removes a ConnectionListener.
	 * 
	 * The listener will no longer be informed about new or closed connections.
	 * No-op if the listener was not registered.
	 * 
	 * @param listener the ConnectionListener to remove.
	 */
	public void removeConnectionListener(final ConnectionListener listener) {
		delegatingConnectionListener.removeListener(listener);
	}
}
