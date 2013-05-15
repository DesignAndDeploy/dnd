package edu.teco.dnd.eclipse;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;

import lime.LimeServer;
import lime.PropertyKeys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * The Activator class loads the plug-in when Eclipse is started.
 */
public class Activator extends AbstractUIPlugin {
	/**
	 * Logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Activator.class);

	/** The name of the plug-in. */
	public static final String PLUGIN_ID = "Design And Deploy";
	public static final String WANTED_IP = "Current IP Address";

	public static final String AUTOMATIC_IP_ADDRESS = "Automatic";

	/** The lime server used by this plug-in. */
	private static LimeServer server = null;

	static IEclipsePreferences prefs = null;

	private static final Collection<String> ipAddresses = new HashSet<>();

	private static String wantedIPAddress;

	private static boolean wantedUsed = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public synchronized void start(BundleContext context) throws Exception {
		super.start(context);
		loadPluginSettings();
		loadIPAddresses();

		if (server == null) {
			// start LIME server
			LOGGER.info("starting Lime server");
			server = LimeServer.getServer();
			// set ip address if set
			if (!AUTOMATIC_IP_ADDRESS.equals(wantedIPAddress) && ipAddresses.contains(wantedIPAddress)) {
				wantedUsed = true;
				server.setProperty(PropertyKeys.LOCALADDRkey, wantedIPAddress);
			}
			server.setProperty(PropertyKeys.GM_DETECTORkey, "beaconing");
			server.boot();
			server.engage();
			LOGGER.info("Lime server started");
		}
	}

	/**
	 * Loads all IP addresses of available interfaces.
	 */
	private void loadIPAddresses() {
		ipAddresses.clear();
		Enumeration<NetworkInterface> nifs = null;
		try {
			nifs = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
		}
		while (nifs != null && nifs.hasMoreElements()) {
			NetworkInterface ni = nifs.nextElement();
			LOGGER.trace("examining {}", ni);

			for (Enumeration<InetAddress> addrs = ni.getInetAddresses(); addrs.hasMoreElements();) {
				InetAddress inetAddress = addrs.nextElement();
				LOGGER.trace("Found inet address " + inetAddress);
				if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
					ipAddresses.add(inetAddress.getHostAddress());
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (server != null) {// shut down LIME server
			LOGGER.info("shutting down Lime Server");
			server.disengage();
			server.shutdown(false);
			LOGGER.info("Lime server shut down");
		}
		// plugin = null;
		super.stop(context);
	}

	public static Collection<String> getIPAddresses() {
		return ipAddresses;
	}

	/**
	 * Stores the given IP address as the desired IP address.
	 * 
	 * @param ipAddress
	 *            IP address to store
	 */
	public static void storeIPAddress(String ipAddress) {
		if (prefs != null) {
			prefs.put(WANTED_IP, ipAddress);
			try {
				prefs.flush();
				LOGGER.info("stored ip address " + ipAddress);
			} catch (BackingStoreException e) {
				LOGGER.warn("Couldn't store IP address.");
				LOGGER.catching(e);
			}
		} else {
			LOGGER.warn("Preferences not loaded!");
		}
	}

	/**
	 * Loads the plug-in preferences stored: the wanted IP address.
	 */
	private void loadPluginSettings() {
		prefs = ConfigurationScope.INSTANCE.getNode("edu.teco.dnd.eclipse");
		if (prefs != null) {
			try {
				// IP address
				wantedIPAddress = prefs.get(WANTED_IP, AUTOMATIC_IP_ADDRESS);
				LOGGER.info("Loaded wanted ip address" + wantedIPAddress);
			} catch (
					IllegalStateException | NullPointerException e) {
				LOGGER.warn("Could not read from prefernces!");
				useBackupSettings();
			}

		} else {
			LOGGER.warn("Could not load preferences!");
			useBackupSettings();
		}
	}

	/**
	 * Loads backup values when preferences couldn't be loaded.
	 */
	private void useBackupSettings() {
		wantedIPAddress = AUTOMATIC_IP_ADDRESS;
	}

	/**
	 * Returns the IP set by the user
	 * 
	 * @return the ip set by the user
	 */
	public static String getWantedIPAddress() {
		return wantedIPAddress;
	}

	public static String getUsedIPAddress() {
		return wantedUsed ? wantedIPAddress : AUTOMATIC_IP_ADDRESS;
	}
}
