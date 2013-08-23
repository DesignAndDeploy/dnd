package edu.teco.dnd.command;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioDatagramChannel;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.teco.dnd.eclipse.DNDServerStateListener;
import edu.teco.dnd.eclipse.ModuleManager;
import edu.teco.dnd.module.ModuleMain;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.TCPConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.util.NetConnection;

/**
 * This class is responsible for managing all server activities for the command line program.
 * @author jung
 *
 */
public class ServerManager {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ServerManager.class);

	/**
	 * The default port to listen on for incoming connections.
	 */
	public static final int DEFAULT_LISTEN_PORT = 5000;

	/**
	 * The default address used for multicast.
	 */
	public static final InetSocketAddress DEFAULT_MULTICAST_ADDRESS = ModuleMain.DEFAULT_MULTICAST_ADDRESS;

	private final ReadWriteLock serverStateLock = new ReentrantReadWriteLock();

	private ConnectionManager connectionManager;

	private UUID uuid;

	private UDPMulticastBeacon beacon;

	private final List<EventExecutorGroup> eventExecutorGroups = new ArrayList<EventExecutorGroup>();

	private final Set<DNDServerStateListener> serverStateListener = new HashSet<DNDServerStateListener>();

	private String multicastAddress;
	
	private String listenAddress;
	
	private String announceAddress;
	
	/**
	 * Creates a new ServerManager for the command line program.
	 * @param multicast String representing the multicast address.
	 * @param listen String representing the listen address.
	 * @param announce String representing the announce address.
	 */
	public ServerManager(String multicast, String listen, String announce) {
		uuid = UUID.randomUUID();
		multicastAddress = multicast;
		listenAddress = listen;
		announceAddress = announce;
	}

	public void startServer() {
		LOGGER.entry();
		serverStateLock.readLock().lock();
		try {
			if (connectionManager != null && !connectionManager.getShutdownFuture().isDone()) {
				LOGGER.exit();
				return;
			}
		} finally {
			serverStateLock.readLock().unlock();
		}

		TCPConnectionManager connectionManager = null;
		EventLoopGroup networkEventLoopGroup = null;
		final List<InetSocketAddress> listen = getListenSUBSTITUTE();
		final List<InetSocketAddress> announce = getAnnounceSUBSTITUTE();
		final List<NetConnection> multicast = getMulticastSUBSTITUTE();

		serverStateLock.writeLock().lock();
		try {
			if (this.connectionManager != null) {
				LOGGER.exit();
				return;
			}

			networkEventLoopGroup = new NioEventLoopGroup();
			OioEventLoopGroup oioEventLoopGroup = new OioEventLoopGroup();
			eventExecutorGroups.add(networkEventLoopGroup);
			eventExecutorGroups.add(oioEventLoopGroup);

			connectionManager =
					new TCPConnectionManager(networkEventLoopGroup, networkEventLoopGroup,
							new ChannelFactory<NioServerSocketChannel>() {
								@Override
								public NioServerSocketChannel newChannel() {
									return new NioServerSocketChannel();
								}
							}, new ChannelFactory<NioSocketChannel>() {
								@Override
								public NioSocketChannel newChannel() {
									return new NioSocketChannel();
								}
							}, uuid);
			ModuleMain.globalRegisterMessageAdapterType(connectionManager);
			this.connectionManager = connectionManager;

			beacon = new UDPMulticastBeacon(new ChannelFactory<OioDatagramChannel>() {
				@Override
				public OioDatagramChannel newChannel() {
					return new OioDatagramChannel();
				}
			}, oioEventLoopGroup, networkEventLoopGroup, uuid);

			for (final DNDServerStateListener listener : serverStateListener) {
				listener.serverStarted(connectionManager, beacon);
			}
		} finally {
			serverStateLock.writeLock().unlock();
		}
		beacon.addListener(connectionManager);
		List<InetSocketAddress> beaconAddresses;
		if (announce.isEmpty()) {
			beaconAddresses = new ArrayList<InetSocketAddress>();
			if (listen.isEmpty()) {
				try {
					for (final InetAddress address : resolveAllAddresses(getAllLocalAddresses())) {
						beaconAddresses.add(new InetSocketAddress(address, DEFAULT_LISTEN_PORT));
					}
				} catch (final SocketException e) {
					LOGGER.catching(Level.WARN, e);
					errorExit();
				}
			} else {
				// TODO: will probably return non-optimal result if 0.0.0.0 is used in listen
				for (final InetSocketAddress address : listen) {
					for (final InetAddress addr : resolveAddress(address.getAddress())) {
						beaconAddresses.add(new InetSocketAddress(addr, address.getPort()));
					}
				}
			}
		} else {
			beaconAddresses = announce;
		}
		beacon.setAnnounceAddresses(beaconAddresses);

		final TCPConnectionManager conMan = connectionManager;
		networkEventLoopGroup.execute(new Runnable() {
			@Override
			public void run() {
				if (listen.isEmpty()) {
					conMan.startListening(new InetSocketAddress(DEFAULT_LISTEN_PORT));
				} else {
					for (final InetSocketAddress address : listen) {
						conMan.startListening(address);
					}
				}

				if (multicast.isEmpty()) {
					try {
						beacon.addAddress(DEFAULT_MULTICAST_ADDRESS);
					} catch (final SocketException e) {
						LOGGER.catching(Level.WARN, e);
						errorExit();
					}
				} else {
					for (final NetConnection netConnection : multicast) {
						beacon.addAddress(netConnection.getInterface(), netConnection.getAddress());
					}
				}
			}
		});

		LOGGER.exit();
	}

	private static Set<InetAddress> getAllLocalAddresses() throws SocketException {
		final Set<InetAddress> localAddresses = new HashSet<InetAddress>();
		final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			final Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
			while (addresses.hasMoreElements()) {
				localAddresses.add(addresses.nextElement());
			}
		}
		return localAddresses;
	}

	/**
	 * Tries to get as many address out of the given ones. This includes DNS lookups and reverse DNS lookups.
	 * 
	 * @param addresses
	 *            the addresses to start from
	 * @return all addresses that describe any of the given ones
	 */
	private static Set<InetAddress> resolveAddress(InetAddress address) {
		if (address == null) {
			return Collections.emptySet();
		}
		final Set<InetAddress> resolvedAddresses = new HashSet<InetAddress>();
		final String hostname = address.getCanonicalHostName();
		try {
			resolvedAddresses.add(InetAddress.getByName(hostname));
		} catch (final UnknownHostException e) {
			LOGGER.catching(Level.DEBUG, e);
			errorExit();
		}
		try {
			for (final InetAddress addr : InetAddress.getAllByName(hostname)) {
				resolvedAddresses.add(addr);
			}
		} catch (final UnknownHostException e) {
			LOGGER.catching(Level.DEBUG, e);
			errorExit();
		}
		try {
			resolvedAddresses.add(InetAddress.getByName(address.getHostAddress()));
		} catch (final UnknownHostException e) {
			LOGGER.catching(Level.DEBUG, e);
			errorExit();
		}
		return resolvedAddresses;
	}

	private static Set<InetAddress> resolveAllAddresses(final Collection<InetAddress> addresses) {
		final Set<InetAddress> resolvedAddresses = new HashSet<InetAddress>();
		for (final InetAddress address : addresses) {
			resolvedAddresses.addAll(resolveAddress(address));
		}
		return resolvedAddresses;
	}

	public void shutdownServer() {
		LOGGER.entry();
		serverStateLock.readLock().lock();
		try {
			if (connectionManager == null) {
				LOGGER.exit();
				return;
			}
		} finally {
			serverStateLock.readLock().unlock();
		}

		serverStateLock.writeLock().lock();
		try {
			if (connectionManager == null) {
				return;
			}
			connectionManager.shutdown();
			beacon.shutdown();
		} finally {
			serverStateLock.writeLock().unlock();
		}
		new Thread() {
			private void await(final Future<?> future) {
				while (!future.isDone()) {
					try {
						future.get();
					} catch (InterruptedException e) {
						errorExit();
					} catch (ExecutionException e) {
						errorExit();
						break;
					}
				}
			}

			@Override
			public void run() {
				await(connectionManager.getShutdownFuture());
				await(beacon.getShutdownFuture());

				serverStateLock.writeLock().lock();
				try {
					connectionManager = null;
					beacon = null;
					for (final EventExecutorGroup group : eventExecutorGroups) {
						group.shutdownGracefully();
					}
					eventExecutorGroups.clear();
					for (final DNDServerStateListener listener : serverStateListener) {
						listener.serverStopped();
					}
				} finally {
					serverStateLock.writeLock().unlock();
				}
			}
		}.start();
		LOGGER.exit();
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public UDPMulticastBeacon getMulticastBeacon() {
		return beacon;
	}

	public void addServerStateListener(final DNDServerStateListener listener) {
		serverStateLock.writeLock().lock();
		try {
			serverStateListener.add(listener);
			if (connectionManager == null) {
				listener.serverStopped();
			} else {
				listener.serverStarted(connectionManager, beacon);
			}
		} finally {
			serverStateLock.writeLock().unlock();
		}
	}

	public void removeServerStateListener(final DNDServerStateListener listener) {
		serverStateLock.writeLock().lock();
		try {
			serverStateListener.remove(listener);
		} finally {
			serverStateLock.writeLock().unlock();
		}
	}

	public boolean isRunning() {
		LOGGER.entry();
		serverStateLock.readLock().lock();
		try {
			if (connectionManager == null || connectionManager.isShuttingDown()) {
				LOGGER.exit(false);
				return false;
			} else {
				LOGGER.exit(true);
				return true;
			}
		} finally {
			serverStateLock.readLock().unlock();
		}
	}

	/**
	 * The following three methods use hard coded addresses. They substitute for getListen, getAnnounce and getMulticast
	 * until configurations can get passed to the program otherwise.
	 * 
	 * @return List of Addresses for listening, announcing and multicasting.
	 */
	private List<InetSocketAddress> getListenSUBSTITUTE() {
		final List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(1);
		String item = listenAddress;
		final String[] parts = item.split(":", 2);
		if (parts.length != 2) {
			return null;
		}
		try {
			addresses.add(new InetSocketAddress(parts[0], Integer.valueOf(parts[1])));
		} catch (final NumberFormatException e) {
			errorExit("Invalid listen address.");
		}
		return addresses;
	}

	private List<InetSocketAddress> getAnnounceSUBSTITUTE() {
		final List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(1);
		String item = announceAddress;
		final String[] parts = item.split(":", 2);
		if (parts.length != 2) {
			return null;
		}
		try {
			addresses.add(new InetSocketAddress(parts[0], Integer.valueOf(parts[1])));
		} catch (final NumberFormatException e) {
			errorExit("Invalid announce address.");
		}
		return addresses;
	}

	private List<NetConnection> getMulticastSUBSTITUTE() {
		final List<NetConnection> addresses = new ArrayList<NetConnection>(1);
		String item = multicastAddress;
		final String[] parts = item.split(":", 3);
		if (parts.length != 3) {
			return null;
		}
		try {
			addresses.add(new NetConnection(new InetSocketAddress(parts[0], Integer.valueOf(parts[1])),
					NetworkInterface.getByName(parts[2])));
		} catch (final NumberFormatException e) {
			errorExit("Invalid multicast address.");
		} catch (final SocketException e) {
			errorExit("Invalid multicast address.");
		}
		return addresses;
	}
	
	private void errorExit(String text){
		System.err.println(text);
		System.exit(1);
	}
	
	private static void errorExit(){
		System.err.println("Something went wrong. Ending program.");
		System.exit(1);
	}

}
