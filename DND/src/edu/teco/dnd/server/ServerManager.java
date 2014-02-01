package edu.teco.dnd.server;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

/**
 * Handles starting and stopping the servers for communication between Modules.
 * 
 * @param <C>
 *            the type of configuration the implementation of this class needs
 * @see TCPUDPServerManager
 */
public abstract class ServerManager<C extends ServerConfig> {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ModuleManager moduleManager;
	private final ApplicationManager applicationManager;
	private ConnectionManager connectionManager = null;
	private UDPMulticastBeacon beacon = null;
	private ServerState currentState = ServerState.STOPPED;

	private final Collection<ServerStateListener> listeners = new ArrayList<ServerStateListener>();

	public ServerManager() {
		this.moduleManager = new ModuleManager(this);
		this.applicationManager = new ApplicationManager(this);
	}

	/**
	 * Starts the servers. If the servers are already running, nothing is done.
	 * 
	 * @param serverConfig
	 *            a configuration that describes how the servers should be set up
	 */
	public void startServer(final C serverConfig) {
		synchronized (this) {
			LOGGER.entry(serverConfig);
			if (currentState != ServerState.STOPPED) {
				LOGGER.debug("current state is {}, not starting", currentState);
				LOGGER.exit();
				return;
			}

			LOGGER.debug("notifying listeners that we're starting");
			currentState = ServerState.STARTING;
			notifyListeners();
		}

		LOGGER.debug("intializing server");
		final FutureNotifier<Void> initializeFuture = initializeServer(serverConfig);
		initializeFuture.addListener(new FutureListener<FutureNotifier<Void>>() {
			@Override
			public void operationComplete(final FutureNotifier<Void> future) {
				if (future.isSuccess()) {
					initializeCompleted();
				} else {
					initializeFailed(future.cause());
				}
			}
		});

		LOGGER.exit();
	}

	private synchronized void initializeCompleted() {
		LOGGER.entry();
		LOGGER.debug("notifying listeners that we're running");
		currentState = ServerState.RUNNING;
		notifyListeners();
		LOGGER.exit();
	}

	private synchronized void initializeFailed(final Throwable cause) {
		LOGGER.entry();
		LOGGER.warn("starting server failed", cause);

		connectionManager = null;
		beacon = null;

		LOGGER.debug("notifying listeners that we're stopped again");
		currentState = ServerState.STOPPED;
		notifyListeners();
		LOGGER.exit();
	}

	/**
	 * This method is called after the listeners have been informed that the servers will be starting. The
	 * implementation should set up the servers based on the given serverConfig and tell this ServerManager about them
	 * via {@link #setConnectionManager(ConnectionManager)} and {@link #setBeacon(UDPMulticastBeacon)}.
	 * 
	 * @param serverConfig
	 *            a configuration describing how the servers should be set up
	 */
	protected abstract FutureNotifier<Void> initializeServer(C serverConfig);

	/**
	 * This method is used by implementations to tell this ServerManager about the new ConnectionManager. It must only
	 * be called in {@link #initializeServer(ServerConfig)} and should only be called once.
	 * 
	 * @param connectionManager
	 *            the new ConnectionManager
	 */
	protected synchronized void setConnectionManager(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * This method is used by implementations to tell this ServerManager about the new UDPMulticastBeacon. It must only
	 * be called in {@link #initializeServer(ServerConfig)} and should only be called once.
	 * 
	 * @param beacon
	 *            the new UDPMulticastBeacon
	 */
	protected synchronized void setBeacon(final UDPMulticastBeacon beacon) {
		this.beacon = beacon;
	}

	/**
	 * Stops the servers. If the servers aren't running nothing is done.
	 */
	public synchronized void shutdownServer() {
		LOGGER.entry();
		if (currentState != ServerState.RUNNING) {
			LOGGER.debug("current state is {}, not stopping", currentState);
			return;
		}

		LOGGER.debug("notifying listeners that we're stopping");
		currentState = ServerState.STOPPING;
		notifyListeners();

		LOGGER.debug("deinitializing server");
		final FutureNotifier<Void> deinitializeFuture = deinitializeServer();
		deinitializeFuture.addListener(new FutureListener<FutureNotifier<Void>>() {
			@Override
			public void operationComplete(final FutureNotifier<Void> future) {
				if (future.isSuccess()) {
					deinitializeCompleted();
				} else {
					deinitializeFailed(future.cause());
				}
			}
		});
	}

	private synchronized void deinitializeCompleted() {
		LOGGER.entry();
		connectionManager = null;
		beacon = null;

		LOGGER.debug("notifying listeners that we're stopped");
		currentState = ServerState.STOPPED;
		notifyListeners();
		LOGGER.exit();
	}

	private synchronized void deinitializeFailed(final Throwable cause) {
		LOGGER.entry();
		LOGGER.warn("shutdown failed", cause);

		LOGGER.debug("notifying listeners that we're still running");
		currentState = ServerState.RUNNING;
		notifyListeners();
		LOGGER.exit();
	}

	/**
	 * This method is used to shut down the servers previously created. It should not call
	 * {@link #setBeacon(UDPMulticastBeacon)} or {@link #setConnectionManager(ConnectionManager)}. It is called after
	 * the listeners have been informed that the servers will be shut down and once the returned future finished, the
	 * listeners are informed that the server is stopped.
	 * 
	 * @return a FutureNotifier that finishes once the servers are shut down
	 */
	protected abstract FutureNotifier<Void> deinitializeServer();

	/**
	 * Returns an ApplicationManager that uses this ServerManager. This method should be used instead of creating new
	 * ApplicationManagers.
	 * 
	 * @return an ApplicationManager that uses this ServerManager
	 */
	public ApplicationManager getApplicationManager() {
		return applicationManager;
	}

	/**
	 * Returns the ConnectionManager currently in use. Returns null if the servers are not running.
	 * 
	 * @return the ConnectionManager currently in use
	 */
	public synchronized ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	/**
	 * Returns a ModuleManager that uses this ServerManager. This method should be used instead of creating a new
	 * ModuleManager.
	 * 
	 * @return a ModuleManager that uses this ServerManager
	 */
	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	/**
	 * Returns the UDPMulticastBeacon currently in use. Returns null if the servers are not running.
	 * 
	 * @return the UDPMulticastBeacon currently in use
	 */
	public synchronized UDPMulticastBeacon getBeacon() {
		return beacon;
	}

	public synchronized void addServerStateListener(final ServerStateListener listener) {
		LOGGER.debug("adding listener {}", listener);
		listeners.add(listener);
	}

	public synchronized void removeServerStateListener(final ServerStateListener listener) {
		LOGGER.debug("removing listener {}", listener);
		listeners.remove(listener);
	}

	public synchronized ServerState getState() {
		return currentState;
	}

	public boolean isRunning() {
		return getState() == ServerState.RUNNING;
	}

	private synchronized void notifyListeners() {
		for (final ServerStateListener listener : listeners) {
			try {
				listener.serverStateChanged(currentState, connectionManager, beacon);
			} catch (final Throwable t) {
				LOGGER.catching(t);
			}
		}
	}
}
