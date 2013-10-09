package edu.teco.dnd.network.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.BeaconListener;
import edu.teco.dnd.network.ConnectionListener;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.BeaconMessage;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.network.tcp.ResponseFutureManager.ResponseFutureNotifier;
import edu.teco.dnd.util.FinishedFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

public class TCPConnectionManager implements ConnectionManager, BeaconListener {
	private static final Logger LOGGER = LogManager.getLogger(TCPConnectionManager.class);

	private final ClientChannelManager clientChannelManager;
	private final ServerChannelManager serverChannelManager;
	private final ResponseFutureManager responseFutureManager;
	private final ClientMessageDispatcher messageDispatcher;
	private final ClientChannelInitializer clientChannelInitializer;
	
	private final UUID localUUID;

	private boolean isShuttingDown = false;
	protected ShutdownFuture shutdownFuture = new ShutdownFuture();


	public TCPConnectionManager(final ServerChannelFactory serverChannelFactory,
			final ClientChannelFactory clientChannelFactory, final UUID localUUID) {
		this.localUUID = localUUID;
		responseFutureManager = new ResponseFutureManager();
		clientChannelManager = new ClientChannelManager(clientChannelFactory);
		clientChannelInitializer = new ClientChannelInitializer(clientChannelManager, localUUID);
		clientChannelFactory.setChannelInitializer(clientChannelInitializer);
		messageDispatcher = new ClientMessageDispatcher(clientChannelManager, responseFutureManager);
		clientChannelInitializer.setMessageHandler(messageDispatcher);
		serverChannelFactory.setChildChannelInitializer(clientChannelInitializer);
		serverChannelManager = new ServerChannelManager(serverChannelFactory);
	}

	@Override
	public FutureNotifier<Response> sendMessage(final UUID uuid, final Message message) {
		try {
			final Channel channel = getActiveChannel(uuid);
			final ResponseFutureNotifier futureNotifier = responseFutureManager.createResponseFuture(message.getUUID());
			channel.writeAndFlush(message).addListener(new ResponseInvalidator(futureNotifier));
			return futureNotifier;
		} catch (final NoSuchElementException e) {
			return new FinishedFutureNotifier<Response>(e);
		}
	}

	private Channel getActiveChannel(final UUID uuid) {
		for (final Channel channel : clientChannelManager.getChannels(uuid)) {
			if (clientChannelManager.isActive(channel)) {
				return channel;
			}
		}
		throw new NoSuchElementException("no active channel for " + uuid);
	}

	@Override
	public <T extends Message> void addHandler(final UUID appid, final Class<? extends T> msgType,
			final MessageHandler<? super T> handler, final Executor executor) {
		LOGGER.warn("executors not implemented");
		addHandler(appid, msgType, handler);
	}

	@Override
	public <T extends Message> void addHandler(final UUID appid, final Class<? extends T> msgType,
			final MessageHandler<? super T> handler) {
		messageDispatcher.setHandler(msgType, handler, appid);
	}

	@Override
	public <T extends Message> void addHandler(final Class<? extends T> msgType,
			final MessageHandler<? super T> handler, final Executor executor) {
		LOGGER.warn("executors not implemented");
		addHandler(msgType, handler);
	}

	@Override
	public <T extends Message> void addHandler(final Class<? extends T> msgType, final MessageHandler<? super T> handler) {
		messageDispatcher.setDefaultHandler(msgType, handler);
	}

	@Override
	public Collection<UUID> getConnectedModules() {
		final Set<UUID> moduleUUIDs = new HashSet<UUID>();
		for (final Channel channel : clientChannelManager.getChannels()) {
			if (clientChannelManager.isActive(channel)) {
				final UUID moduleUUID = clientChannelManager.getRemoteUUID(channel);
				if (moduleUUID != null) {
					moduleUUIDs.add(moduleUUID);
				}
			}
		}
		return moduleUUIDs;
	}

	@Override
	public void addConnectionListener(final ConnectionListener listener) {
		clientChannelManager.addConnectionListener(listener);
	}

	@Override
	public void removeConnectionListener(final ConnectionListener listener) {
		clientChannelManager.removeConnectionListener(listener);
	}

	@Override
	public void shutdown() {
		LOGGER.entry();
		synchronized (this) {
			if (isShuttingDown) {
				LOGGER.exit();
				return;
			}

			LOGGER.info("shutting down");
			isShuttingDown = true;

			final FutureNotifier<Collection<Void>> serverCloseFuture = closeAllServerSockets();
			serverCloseFuture.addListener(new FutureListener<FutureNotifier<Collection<Void>>>() {
				@Override
				public void operationComplete(final FutureNotifier<Collection<Void>> future) throws Exception {
					shutdownFuture.setComplete();
				}
			});
		}
		LOGGER.exit();
	}

	private FutureNotifier<Collection<Void>> closeAllServerSockets() {
		return serverChannelManager.closeAllChannels();
	}

	@Override
	public synchronized boolean isShuttingDown() {
		return isShuttingDown;
	}

	@Override
	public FutureNotifier<Void> getShutdownFuture() {
		return shutdownFuture;
	}

	public void startListening(final InetSocketAddress listenAddress) {
		LOGGER.entry(listenAddress);
		synchronized (this) {
			if (!isShuttingDown) {
				LOGGER.info("listening on {}", listenAddress);
				serverChannelManager.bind(listenAddress);
			}
		}
		LOGGER.exit();
	}

	@Override
	public void beaconFound(final BeaconMessage beacon) {
		if (localUUID.equals(beacon.getModuleUUID())) {
			return;
		}
		if (!hasActiveChannel(beacon.getModuleUUID())) {
			for (final InetSocketAddress address : beacon.getAddresses()) {
				if (address != null) {
					connectTo(address);
				}
			}
		}
	}

	private boolean hasActiveChannel(final UUID moduleUUID) {
		try {
			getActiveChannel(moduleUUID);
			return true;
		} catch (final NoSuchElementException e) {
			return false;
		}
	}

	public void connectTo(final InetSocketAddress address) {
		LOGGER.debug("trying to connect to {}", address);
		clientChannelManager.connect(address);
	}
	
	public void registerTypeAdapter(final Type type, final Object adapter) {
		clientChannelInitializer.registerTypeAdapter(type, adapter);
	}
	
	public void addMessageType(final Class<? extends Message> cls) {
		clientChannelInitializer.addMessageType(cls);
	}
}
