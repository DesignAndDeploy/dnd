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

import edu.teco.dnd.eclipse.preferences.PreferencesNetwork;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.network.logging.Log4j2LoggerFactory;
import edu.teco.dnd.server.AddressBasedServerConfig;
import edu.teco.dnd.server.ServerManager;
import edu.teco.dnd.server.TCPUDPServerManager;
import edu.teco.dnd.util.NetConnection;

public class Activator extends AbstractUIPlugin {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Activator.class);

	private static Activator plugin;

	private ServerManager<AddressBasedServerConfig> serverManager = null;
	private ModuleID moduleID = null;

	static {
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

	public ServerManager<AddressBasedServerConfig> getServerManager() {
		return serverManager;
	}

	/**
	 * Starts a server.
	 * 
	 * Use this method to start the server from within eclipse. Activator will get the address arguments from the
	 * preference store and pass them to the {@link ServerManager}. The ServerManager will do the main process of
	 * starting a server, but only Activator can access the Preferences to get the user input in eclipse.
	 * 
	 * When starting a server from outside of eclipse, you can't access the preferences set in eclipse. In that case,
	 * start the server directly through the {@link ServerManager}. You will also have to get the address arguments from
	 * somewhere else.
	 */
	public void startServer() {
		final AddressBasedServerConfig serverConfig =
				new AddressBasedServerConfig(moduleID, getListenAddresses(), getMulticastAddresses(),
						getAnnounceAddresses(), getAnnounceInterval());
		serverManager.startServer(serverConfig);
	}

	public void shutdownServer() {
		serverManager.shutdownServer();
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(PreferencesNetwork.LISTEN_PREFERENCE, "");
		store.setDefault(PreferencesNetwork.MULTICAST_PREFERENCE, "");
		store.setDefault(PreferencesNetwork.ANNOUNCE_PREFERENCE, "");
		store.setDefault(PreferencesNetwork.BEACON_PREFERENCE, UDPMulticastBeacon.DEFAULT_INTERVAL);
	}

	/**
	 * Returns the listen addresses stored in the Eclipse preferences.
	 * 
	 * @return the listen addresses stored in the Eclipse preferences
	 */
	private Collection<InetSocketAddress> getListenAddresses() {
		return getInetSocketAddresses(getPreference(PreferencesNetwork.LISTEN_PREFERENCE).split(" "));
	}

	/**
	 * Returns the multicast addresses stored in the Eclipse preferences.
	 * 
	 * @return the multicast addresses stored in the Eclipse preferences
	 */
	private Collection<NetConnection> getMulticastAddresses() {
		return getNetConnections(getPreference(PreferencesNetwork.MULTICAST_PREFERENCE).split(" "));
	}

	/**
	 * Returns the announce addresses stored in the Eclipse preferences.
	 * 
	 * @return the announce addresses stored in the Eclipse preferences
	 */
	private Collection<InetSocketAddress> getAnnounceAddresses() {
		return getInetSocketAddresses(getPreference(PreferencesNetwork.ANNOUNCE_PREFERENCE).split(" "));
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
		return getPreferenceStore().getInt(PreferencesNetwork.BEACON_PREFERENCE);
	}
}
