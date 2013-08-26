package edu.teco.dnd.eclipse;

import io.netty.util.internal.logging.InternalLoggerFactory;

import java.net.InetSocketAddress;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.teco.dnd.module.ModuleMain;
import edu.teco.dnd.network.logging.Log4j2LoggerFactory;
import edu.teco.dnd.server.ServerManager;

public class Activator extends AbstractUIPlugin {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Activator.class);

	/**
	 * The default port to listen on for incoming connections.
	 */
	public static final int DEFAULT_LISTEN_PORT = 5000;

	/**
	 * The default address used for multicast.
	 */
	public static final InetSocketAddress DEFAULT_MULTICAST_ADDRESS = ModuleMain.DEFAULT_MULTICAST_ADDRESS;

	private static Activator plugin;

	private static ServerManager serverManager;

	private UUID uuid;

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

		uuid = UUID.randomUUID();
		serverManager = ServerManager.getDefault();

		LOGGER.exit();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		LOGGER.entry();
		super.stop(context);
		plugin = null;

		LOGGER.exit();
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
		String multicastAddress = getAddress("multicast", 3);
		String listenAddress = getAddress("listen", 2);
		String announceAddress = getAddress("announce", 2);
		serverManager.startServer(multicastAddress, listenAddress, announceAddress);
	}

	public void shutdownServer() {
		serverManager.shutdownServer();
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault("listen", "");
		store.setDefault("multicast", "");
		store.setDefault("announce", "");
	}

	private String getAddress(String type, int split) {
		final String[] items = getPreferenceStore().getString(type).split(" ");
		for (final String item : items) {
			final String[] parts = item.split(":", split);
			if (parts.length != split) {
				continue;
			}
			return item;
		}
		return null;
	}

}
