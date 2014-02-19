package edu.teco.dnd.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.module.messages.infoReq.ModuleInfoMessage;
import edu.teco.dnd.module.messages.infoReq.RequestModuleInfoMessage;
import edu.teco.dnd.network.ConnectionListener;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

/**
 * Informs {@link ModuleManagerListener listeners} about connected and disconnected {@link Module}s and automatically
 * queries information about them. If this information arrives the listeners are also informed.
 */
public class ModuleManager implements ServerStateListener, ConnectionListener {
	private static final Logger LOGGER = LogManager.getLogger(ModuleManager.class);

	private final FutureListener<FutureNotifier<Response>> moduleInfoListener = new ModuleInfoListener();
	private final Map<ModuleID, ModuleInfo> modules = new HashMap<ModuleID, ModuleInfo>();
	private final Collection<ModuleManagerListener> listeners = new ArrayList<ModuleManagerListener>();
	private ConnectionManager connectionManager = null;

	/**
	 * Initializes a new ModuleManager. New ModuleManagers are normally created by a ServerManager, see
	 * {@link ServerManager#getModuleManager()}.
	 * 
	 * @param serverManager
	 *            the ServerManager that should be used
	 */
	public ModuleManager(final ServerManager<?> serverManager) {
		serverManager.addServerStateListener(this);
	}

	/**
	 * Returns the currently known {@link Module}s. The Collection is a copy of the internal data and can be modified
	 * freely.
	 * 
	 * @return currently known Modules. Only the IDs of the Modules are guaranteed to be set, other values may be
	 *         <code>null</code>
	 */
	public synchronized Collection<ModuleInfo> getModules() {
		return new ArrayList<ModuleInfo>(modules.values());
	}

	/**
	 * Adds a listener. The listener will be informed about all currently known {@link Module}s via
	 * {@link ModuleManagerListener#moduleAdded(ModuleInfo)} immediately. There are no checks made on whether the
	 * listener was already added. If it is added multiple times, all callbacks are called multiple times and
	 * {@link #removeListener(ModuleManagerListener)} has to be called the same number of times to remove the listener
	 * completely.
	 * 
	 * @param listener
	 *            the listener to add. Will be informed about currently known Modules.
	 */
	public synchronized void addListener(final ModuleManagerListener listener) {
		LOGGER.entry(listener);
		listeners.add(listener);

		for (final ModuleInfo module : modules.values()) {
			informModuleAdded(listener, module);
		}
		LOGGER.exit();
	}

	/**
	 * Removes a listener. The listener will no longer be informed about {@link Module}s. If the listener was added
	 * multiple times, it has to be removed the same number of times.
	 * 
	 * @param listener
	 *            the listener to remove. If it was not added, nothing is done
	 * @see #addListener(ModuleManagerListener)
	 */
	public synchronized void removeListener(final ModuleManagerListener listener) {
		LOGGER.entry(listener);
		listeners.remove(listener);
		LOGGER.exit();
	}

	@Override
	public synchronized void serverStateChanged(final ServerState state, final ConnectionManager connectionManager,
			final UDPMulticastBeacon beacon) {
		LOGGER.entry(state, connectionManager, beacon);
		switch (state) {
		case STOPPING:
			if (connectionManager != null) {
				connectionManager.removeConnectionListener(this);
			}
			// fallthrough
		case STOPPED:
			this.connectionManager = null;
			for (final ModuleInfo module : modules.values()) {
				informAllModuleRemoved(module);
			}
			break;

		case RUNNING:
			this.connectionManager = connectionManager;
			if (connectionManager != null) {
				connectionManager.addConnectionListener(this);
			}
			break;
		}
		LOGGER.exit();
	}

	@Override
	public synchronized void connectionEstablished(final ModuleID moduleID) {
		LOGGER.entry();
		if (!modules.containsKey(moduleID)) {
			final ModuleInfo module = new ModuleInfo(moduleID);
			modules.put(moduleID, module);
			queryModuleInfo(moduleID);
			informAllModuleAdded(module);
		}
		LOGGER.exit();
	}

	private synchronized void queryModuleInfo(final ModuleID moduleID) {
		if (connectionManager != null) {
			connectionManager.sendMessage(moduleID, new RequestModuleInfoMessage()).addListener(moduleInfoListener);
		}
	}

	private void informAllModuleAdded(final ModuleInfo module) {
		LOGGER.entry(module);
		for (final ModuleManagerListener listener : listeners) {
			informModuleAdded(listener, module);
		}
		LOGGER.exit();
	}

	private void informModuleAdded(final ModuleManagerListener listener, final ModuleInfo module) {
		LOGGER.entry(listener, module);
		try {
			listener.moduleAdded(module);
		} catch (final Throwable t) {
			LOGGER.warn("listener {} threw {} while adding {}", module);
		}
		LOGGER.exit();
	}

	@Override
	public synchronized void connectionClosed(final ModuleID moduleID) {
		LOGGER.entry(moduleID);
		final ModuleInfo module = modules.remove(moduleID);
		if (module != null) {
			informAllModuleRemoved(module);
		}
		LOGGER.exit();
	}

	private void informAllModuleRemoved(final ModuleInfo module) {
		for (final ModuleManagerListener listener : listeners) {
			informModuleRemoved(listener, module);
		}
	}

	private void informModuleRemoved(final ModuleManagerListener listener, final ModuleInfo module) {
		LOGGER.entry(listener, module);
		try {
			listener.moduleRemoved(module);
		} catch (final Throwable t) {
			LOGGER.warn("listener {} threw {} while removing {}", module);
		}
		LOGGER.exit();
	}

	public synchronized void update() {
		modules.clear();
		for (final ModuleID moduleID : connectionManager.getConnectedModules()) {
			connectionEstablished(moduleID);
		}
	}

	private synchronized void moduleInfoReceived(final ModuleInfo module) {
		LOGGER.entry(module);
		if (modules.put(module.getID(), module) == null) {
			informAllModuleAdded(module);
		} else {
			informAllModuleUpdated(module);
		}
		LOGGER.exit();
	}

	private void informAllModuleUpdated(ModuleInfo module) {
		for (final ModuleManagerListener listener : listeners) {
			informModuleUpdated(listener, module);
		}
	}

	private void informModuleUpdated(final ModuleManagerListener listener, final ModuleInfo module) {
		try {
			listener.moduleUpdated(module);
		} catch (final Throwable t) {
			LOGGER.warn("listener {} threw {} while updating {}", listener, t, module);
		}
	}

	/**
	 * Reacts to {@link ModuleInfoMessage}s by calling {@link ModuleManager#moduleInfoReceived(ModuleInfo)}.
	 */
	private class ModuleInfoListener implements FutureListener<FutureNotifier<Response>> {
		@Override
		public void operationComplete(final FutureNotifier<Response> future) {
			if (future.isSuccess()) {
				final Response response = future.getNow();
				if (response instanceof ModuleInfoMessage) {
					final ModuleInfoMessage moduleInfoMessage = (ModuleInfoMessage) response;
					moduleInfoReceived(moduleInfoMessage.getModule());
				}
			}
		}
	}
}
