package edu.teco.dnd.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ChannelFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioDatagramChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.network.tcp.ClientBootstrapChannelFactory;
import edu.teco.dnd.network.tcp.ServerBootstrapChannelFactory;
import edu.teco.dnd.network.tcp.TCPConnectionManager;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;
import edu.teco.dnd.util.NetConnection;

/**
 * A ServerManager that starts a {@link TCPConnectionManager} and an {@link UDPMulticastBeacon}.
 */
public class TCPUDPServerManager extends ServerManager<AddressBasedServerConfig> {
	private static final Logger LOGGER = LogManager.getLogger();

	private static final OioDatagramChannelFactory OIO_DATAGRAM_CHANNEL_FACTORY = new OioDatagramChannelFactory();

	private final Collection<EventExecutorGroup> eventExecutorGroups = new ArrayList<EventExecutorGroup>();
	private ScheduledExecutorService scheduledExecutorService;

	@Override
	protected FutureNotifier<Void> initializeServer(final AddressBasedServerConfig serverConfig) {
		LOGGER.entry(serverConfig);
		return LOGGER.exit(new InitializeFutureNotifier(serverConfig));
	}

	/**
	 * Initializes a new TCPConnectionManager.
	 * 
	 * @param serverConfig
	 *            a configuration to use for initializing
	 * @return the new ConnectionManager
	 */
	private ConnectionManager initializeConnectionManager(final AddressBasedServerConfig serverConfig) {
		LOGGER.entry();
		final EventLoopGroup applicationEventLoopGroup = new OioEventLoopGroup();
		final EventLoopGroup networkEventLoopGroup = new OioEventLoopGroup();
		eventExecutorGroups.add(applicationEventLoopGroup);
		eventExecutorGroups.add(networkEventLoopGroup);

		final ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(networkEventLoopGroup, applicationEventLoopGroup);
		serverBootstrap.channel(OioServerSocketChannel.class);
		final ServerBootstrapChannelFactory serverChannelFactory = new ServerBootstrapChannelFactory(serverBootstrap);

		final Bootstrap clientBootstrap = new Bootstrap();
		clientBootstrap.group(applicationEventLoopGroup);
		clientBootstrap.channel(OioSocketChannel.class);
		final ClientBootstrapChannelFactory clientChannelFactory = new ClientBootstrapChannelFactory(clientBootstrap);

		final TCPConnectionManager connectionManager =
				new TCPConnectionManager(serverChannelFactory, clientChannelFactory, scheduledExecutorService,
						serverConfig.getModuleID());

		new TCPProtocol().initialize(connectionManager);

		for (final InetSocketAddress address : serverConfig.getListenAddresses()) {
			connectionManager.startListening(address);
		}

		return LOGGER.exit(connectionManager);
	}

	/**
	 * Initializes a new UDPMulticastBeacon.
	 * 
	 * @param serverConfig
	 *            a configuration to use for initializing
	 * @return the new UDPMulticastBeacon
	 */
	private UDPMulticastBeacon initializeBeacon(final AddressBasedServerConfig serverConfig) {
		LOGGER.entry();
		final OioEventLoopGroup networkEventLoopGroup = new OioEventLoopGroup();
		eventExecutorGroups.add(networkEventLoopGroup);

		final UDPMulticastBeacon beacon =
				new UDPMulticastBeacon(OIO_DATAGRAM_CHANNEL_FACTORY, networkEventLoopGroup, scheduledExecutorService,
						serverConfig.getModuleID(), serverConfig.getAnnounceInterval(), TimeUnit.SECONDS);
		beacon.addListener((TCPConnectionManager) getConnectionManager());
		beacon.setAnnounceAddresses(new ArrayList<InetSocketAddress>(serverConfig.getAnnounceAddresses()));

		for (final NetConnection netConnection : serverConfig.getMulticastAddresses()) {
			LOGGER.debug("adding address {} to beacon", netConnection);
			beacon.addAddress(netConnection.getInterface(), netConnection.getAddress());
		}

		return LOGGER.exit(beacon);
	}

	@Override
	protected FutureNotifier<Void> deinitializeServer() {
		LOGGER.entry();
		final DeinitializeFuture deinitializeFuture = new DeinitializeFuture();
		return LOGGER.exit(deinitializeFuture);
	}

	/**
	 * A channel factory returning {@link OioDatagramChannel}s.
	 */
	private static class OioDatagramChannelFactory implements ChannelFactory<OioDatagramChannel> {
		@Override
		public OioDatagramChannel newChannel() {
			return new OioDatagramChannel();
		}
	}

	/**
	 * Used to initialize the servers. The main code is run with the
	 * {@link TCPUDPServerManager#scheduledExecutorService}.
	 */
	private class InitializeFutureNotifier extends DefaultFutureNotifier<Void> implements Runnable {
		private final AddressBasedServerConfig serverConfig;

		private InitializeFutureNotifier(final AddressBasedServerConfig serverConfig) {
			LOGGER.entry();
			this.serverConfig = serverConfig;

			eventExecutorGroups.clear();

			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			scheduledExecutorService.execute(this);
			LOGGER.exit();
		}

		@Override
		public void run() {
			LOGGER.entry();
			setConnectionManager(initializeConnectionManager(serverConfig));
			setBeacon(initializeBeacon(serverConfig));

			setSuccess(null);
			LOGGER.exit();
		}
	}

	/**
	 * Used to deinitialize the servers. The main code is run with the
	 * {@link TCPUDPServerManager#scheduledExecutorService}, afterwards it waits for the shutdownFutures of the
	 * {@link TCPConnectionManager} and {@link UDPMulticastBeacon}.
	 */
	private class DeinitializeFuture extends DefaultFutureNotifier<Void> implements
			FutureListener<FutureNotifier<Void>>, Runnable {
		private final AtomicInteger unfinishedFutures = new AtomicInteger(0);

		private DeinitializeFuture() {
			scheduledExecutorService.execute(this);
		}

		@Override
		public void run() {
			unfinishedFutures.set(2);

			final ConnectionManager connectionManager = getConnectionManager();
			connectionManager.getShutdownFuture().addListener(this);
			connectionManager.shutdown();

			final UDPMulticastBeacon beacon = getBeacon();
			beacon.getShutdownFuture().addListener(this);
			beacon.shutdown();
		}

		@Override
		public void operationComplete(final FutureNotifier<Void> future) {
			LOGGER.entry(future);
			if (unfinishedFutures.decrementAndGet() <= 0) {
				new Thread() {
					@Override
					public void run() {
						LOGGER.debug("all futures finished");
						final Collection<Future<?>> terminationFutures = new ArrayList<Future<?>>();
						for (final EventExecutorGroup eventExecutorGroup : eventExecutorGroups) {
							terminationFutures.add(eventExecutorGroup.shutdownGracefully());
						}

						LOGGER.debug("shutting down ScheduledExecutorService");
						scheduledExecutorService.shutdown();

						LOGGER.debug("waiting for EventExecutorGroups");
						try {
							for (final Future<?> terminationFuture : terminationFutures) {
								LOGGER.trace("awaiting {}", terminationFuture);
								terminationFuture.await();
							}
						} catch (final InterruptedException e) {
						}

						eventExecutorGroups.clear();
						scheduledExecutorService = null;

						LOGGER.debug("setting success");
						setSuccess(null);
					}
				}.start();
			}
			LOGGER.exit();
		}
	}
	
	@Override
	public TCPConnectionManager getConnectionManager() {
		return (TCPConnectionManager) super.getConnectionManager();
	}
}
