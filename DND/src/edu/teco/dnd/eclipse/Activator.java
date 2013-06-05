package edu.teco.dnd.eclipse;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioDatagramChannel;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.TCPConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.network.logging.Log4j2LoggerFactory;
import edu.teco.dnd.util.NetConnection;

public class Activator extends AbstractUIPlugin {
	private static final Logger LOGGER = LogManager.getLogger(Activator.class);
	
	private static Activator plugin;

	private UUID uuid;

	private ConnectionManager connectionManager;

	private UDPMulticastBeacon beacon;
	
	
	static {
		InternalLoggerFactory.setDefaultFactory(new Log4j2LoggerFactory());
	}

	
	public static Activator getDefault() {
		return plugin;
	}
	
	public void startServer() {
		LOGGER.entry();
		final List<InetSocketAddress> listen = getListen();
		final List<InetSocketAddress> announce = getAnnounce();
		final List<NetConnection> multicast = getMulticast();
		
		final NioEventLoopGroup networkEventLoopGroup = new NioEventLoopGroup();

		final TCPConnectionManager connectionManager = new TCPConnectionManager(networkEventLoopGroup,
				networkEventLoopGroup, new ChannelFactory<NioServerSocketChannel>() {
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
		this.connectionManager = connectionManager;

		beacon = new UDPMulticastBeacon(new ChannelFactory<OioDatagramChannel>() {
			@Override
			public OioDatagramChannel newChannel() {
				return new OioDatagramChannel();
			}
		}, new OioEventLoopGroup(), networkEventLoopGroup, uuid);
		beacon.addListener(connectionManager);
		beacon.setAnnounceAddresses(announce);

		networkEventLoopGroup.execute(new Runnable() {
			@Override
			public void run() {
				for (final InetSocketAddress address : listen) {
					connectionManager.startListening(address);
				}
				for (final NetConnection netConnection : multicast) {
					beacon.addAddress(netConnection.getInterface(), netConnection.getAddress());
				}
			}
		});
		
		LOGGER.exit();
	}

	private List<InetSocketAddress> getListen() {
		final String[] items = getPreferenceStore().getString("listen").split(" ");
		final List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(items.length);
		for (final String item : items) {
			final String[] parts = item.split(":", 2);
			if (parts.length != 2) {
				continue;
			}
			try {
				addresses.add(new InetSocketAddress(parts[0], Integer.valueOf(parts[1])));
			} catch (final NumberFormatException e) {
			}
		}
		return addresses;
	}

	private List<InetSocketAddress> getAnnounce() {
		final String[] items = getPreferenceStore().getString("announce").split(" ");
		final List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(items.length);
		for (final String item : items) {
			final String[] parts = item.split(":", 2);
			if (parts.length != 2) {
				continue;
			}
			try {
				addresses.add(new InetSocketAddress(parts[0], Integer.valueOf(parts[1])));
			} catch (final NumberFormatException e) {
			}
		}
		return addresses;
	}

	private List<NetConnection> getMulticast() {
		final String[] items = getPreferenceStore().getString("multicast").split(" ");
		final List<NetConnection> addresses = new ArrayList<NetConnection>(items.length);
		for (final String item : items) {
			final String[] parts = item.split(":", 3);
			if (parts.length != 3) {
				continue;
			}
			try {
				addresses.add(new NetConnection(new InetSocketAddress(parts[0], Integer.valueOf(parts[1])),
						NetworkInterface.getByName(parts[2])));
			} catch (final NumberFormatException e) {
			} catch (final SocketException e) {
			}
		}
		return addresses;
	}
	
	@Override
	public void start(final BundleContext context) throws Exception {
		LOGGER.entry(context);
		super.start(context);
		plugin = this;
		
		uuid = UUID.randomUUID();
		
		startServer();
		LOGGER.exit();
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		LOGGER.entry();
		super.stop(context);
		plugin = null;

		// TODO: shutdown
		LOGGER.exit();
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public UDPMulticastBeacon getMulticastBeacon() {
		return beacon;
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault("listen", "bli");
		store.setDefault("multicast", "bla");
		store.setDefault("announce", "blubb");
	}
}
