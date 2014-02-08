package edu.teco.dnd.eclipse;

import io.netty.util.internal.logging.InternalLoggerFactory;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.teco.dnd.eclipse.preferences.Preferences;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.eclipse.preferences.NetworkPreferences;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.network.logging.Log4j2LoggerFactory;
import edu.teco.dnd.server.AddressBasedServerConfig;
import edu.teco.dnd.server.SimpleAddressBasedServerConfig;
import edu.teco.dnd.server.ApplicationManager;
import edu.teco.dnd.server.ModuleManager;
import edu.teco.dnd.server.ServerManager;
import edu.teco.dnd.server.ServerStateListener;
import edu.teco.dnd.server.TCPUDPServerManager;
import edu.teco.dnd.util.NetConnection;

/**
 * This is the main class for the Eclipse plugin. It is implemented as a singleton and handles global variables such as
 * the {@link ServerManager}.
 */
public class Activator extends AbstractUIPlugin {
	private static final Logger LOGGER = LogManager.getLogger(Activator.class);

	private static Activator plugin;

	private ServerManager<AddressBasedServerConfig> serverManager = null;
	private ModuleID moduleID = null;

	static {
		// set Logger factory of Netty. Must be run as early as possible so that Netty has not created any Loggers
		// before the factory is set.
		InternalLoggerFactory.setDefaultFactory(new Log4j2LoggerFactory());
	}

	public static Activator getDefault() {
		return plugin;
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		LOGGER.entry(context);
		super.start(context);
		plugin = this;
		serverManager = new TCPUDPServerManager();
		moduleID = new ModuleID();
		LOGGER.exit();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		LOGGER.entry();
		super.stop(context);
		plugin = null;
		serverManager = null;
		moduleID = null;
		LOGGER.exit();
	}

	/**
	 * Returns a ServerManager that should be used for registering with a {@link ModuleManager} and/or a
	 * {@link ApplicationManager} as well as for registering as a {@link ServerStateListener} directly. You should not
	 * call {@link ServerManager#startServer(String, String, String, int)} or {@link ServerManager#shutdownServer()}
	 * directly. Use {@link #startServer()} and {@link #shutdownServer()} instead.
	 * 
	 * @return the ServerManager to use
	 */
	public ServerManager<AddressBasedServerConfig> getServerManager() {
		return serverManager;
	}

	/**
	 * Starts the server used to communicate with other Modules. The configuration for the server is retrieved from the
	 * {@link Preferences}.
	 */
	public void startServer() {
		final AddressBasedServerConfig serverConfig =
				new SimpleAddressBasedServerConfig(moduleID, getListenAddresses(), getMulticastAddresses(),
						getAnnounceAddresses(), getAnnounceInterval());
		serverManager.startServer(serverConfig);
	}

	public void shutdownServer() {
		serverManager.shutdownServer();
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(NetworkPreferences.LISTEN_PREFERENCE, "");
		store.setDefault(NetworkPreferences.MULTICAST_PREFERENCE, "");
		store.setDefault(NetworkPreferences.ANNOUNCE_PREFERENCE, "");
		store.setDefault(NetworkPreferences.BEACON_PREFERENCE, UDPMulticastBeacon.DEFAULT_INTERVAL);
	}

	/**
	 * Returns the listen addresses stored in the Eclipse preferences.
	 * 
	 * @return the listen addresses stored in the Eclipse preferences
	 */
	private Collection<InetSocketAddress> getListenAddresses() {
		return getInetSocketAddresses(getPreference(NetworkPreferences.LISTEN_PREFERENCE).split(" "));
	}

	/**
	 * Returns the multicast addresses stored in the Eclipse preferences.
	 * 
	 * @return the multicast addresses stored in the Eclipse preferences
	 */
	private Collection<NetConnection> getMulticastAddresses() {
		return getNetConnections(getPreference(NetworkPreferences.MULTICAST_PREFERENCE).split(" "));
	}

	/**
	 * Returns the announce addresses stored in the Eclipse preferences.
	 * 
	 * @return the announce addresses stored in the Eclipse preferences
	 */
	private Collection<InetSocketAddress> getAnnounceAddresses() {
		return getInetSocketAddresses(getPreference(NetworkPreferences.ANNOUNCE_PREFERENCE).split(" "));
	}

	private Collection<InetSocketAddress> getInetSocketAddresses(final String[] addresses) {
		final Collection<InetSocketAddress> result = new ArrayList<InetSocketAddress>(addresses.length);
		for (final String address : addresses) {
			final String[] splittedAddress = address.split(":");
			if (splittedAddress.length != 2) {
				continue;
			}
			try {
				result.add(new InetSocketAddress(splittedAddress[0], Integer.valueOf(splittedAddress[1])));
			} catch (final NumberFormatException e) {
				continue;
			}
		}
		return result;
	}

	private Collection<NetConnection> getNetConnections(final String[] connections) {
		final Collection<NetConnection> result = new ArrayList<NetConnection>(connections.length);
		for (final String connection : connections) {
			final String[] splittedAddress = connection.split(":");
			if (splittedAddress.length != 3) {
				continue;
			}
			try {
				result.add(new NetConnection(new InetSocketAddress(splittedAddress[0], Integer
						.valueOf(splittedAddress[1])), NetworkInterface.getByName(splittedAddress[2])));
			} catch (final NumberFormatException e) {
				continue;
			} catch (final SocketException e) {
				continue;
			}
		}
		return result;
	}

	private String getPreference(final String preferenceName) {
		return getPreferenceStore().getString(preferenceName);
	}

	/**
	 * Returns the announce interval stored in the Eclipse preferences.
	 * 
	 * @return the announce interval stored in the Eclipse preferences
	 */
	private int getAnnounceInterval() {
		return getPreferenceStore().getInt(NetworkPreferences.BEACON_PREFERENCE);
	}
}
