package edu.teco.dnd.eclipse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.teco.dnd.discover.ModuleQuery;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.network.ConnectionListener;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;
import edu.teco.dnd.util.JoinedFutureNotifier;

/**
 * This class coordinates a List of currently running modules. It provides the views and editors with information to
 * display to the user.
 * 
 * @author jung
 * 
 */
public class ModuleManager implements ConnectionListener, DNDServerStateListener,
		FutureListener<FutureNotifier<Module>> {
	/**
	 * A Map of all Modules that were found. Modules are added with a value of null when they're discovered, after the
	 * Module responded to the RequestModuleInfoMessage the value is updated.
	 */
	private Map<UUID, Module> map;

	/**
	 * All registered listeners.
	 */
	private final Set<ModuleManagerListener> moduleManagerListener = new HashSet<ModuleManagerListener>();

	/**
	 * The currently running ConnectionManager.
	 */
	private ConnectionManager connectionManager;

	/**
	 * The ModuleQuery that is used to query information about the Modules.
	 */
	private ModuleQuery query;

	/**
	 * Initializes a new ModuleManager.
	 */
	public ModuleManager() {
		map = new HashMap<UUID, Module>();

		Activator.getDefault().addServerStateListener(this);
	}

	/**
	 * Registers a new ModuleManagerListener. ModuleManagerListeners can only be registered once, if this method is
	 * called multiple times with the same argument the listener will still only be called once for every event.
	 * 
	 * @param listener
	 *            the ModuleManagerListener to add
	 */
	public synchronized void addModuleManagerListener(final ModuleManagerListener listener) {
		moduleManagerListener.add(listener);
		if (connectionManager == null) {
			listener.serverOffline();
		} else {
			listener.serverOnline(map);
		}
	}

	/**
	 * Removes a ModuleManagerListener. The ModuleManagerListener will no longer receive events generated by this
	 * ModuleManager, even if {@link #addModuleManagerListener(ModuleManagerListener)} was called multiple times. If the
	 * listener was not registered nothing is done.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public synchronized void removeModuleManagerListener(final ModuleManagerListener listener) {
		moduleManagerListener.remove(listener);
	}

	@Override
	public synchronized void serverStarted(ConnectionManager newConnectionManager, UDPMulticastBeacon beacon) {
		if (connectionManager != null) {
			connectionManager.removeConnectionListener(this);
		}
		map.clear();

		connectionManager = newConnectionManager;
		connectionManager.addConnectionListener(this);
		query = new ModuleQuery(connectionManager);
		Collection<UUID> modules = connectionManager.getConnectedModules();

		for (UUID id : modules) {
			map.put(id, null);
			query.getModuleInfo(id).addListener(this);
		}

		for (final ModuleManagerListener listener : moduleManagerListener) {
			listener.serverOnline(map);
		}
	}

	@Override
	public synchronized void serverStopped() {
		map.clear();

		if (connectionManager != null) {
			connectionManager.removeConnectionListener(this);
		}

		for (final ModuleManagerListener listener : moduleManagerListener) {
			listener.serverOffline();
		}
	}

	@Override
	public synchronized void connectionEstablished(UUID uuid) {
		map.put(uuid, null);
		query.getModuleInfo(uuid).addListener(this);

		for (final ModuleManagerListener listener : moduleManagerListener) {
			listener.moduleOnline(uuid);
		}
	}

	@Override
	public synchronized void connectionClosed(UUID uuid) {
		map.remove(uuid);

		for (final ModuleManagerListener listener : moduleManagerListener) {
			listener.moduleOffline(uuid);
		}
	}

	@Override
	public synchronized void operationComplete(FutureNotifier<Module> future) throws Exception {
		if (future.isSuccess()) {
			Module module = future.getNow();
			UUID id = module.getUUID();
			map.put(id, module);

			for (final ModuleManagerListener listener : moduleManagerListener) {
				listener.moduleResolved(id, module);
			}
		}

	}

	/**
	 * Returns a copy of the Map of all currently known Modules. The value for an entry may be null if the Module hasn't
	 * responded to the RequestModuleInfoMessage yet.
	 * 
	 * @return a copy of the Map of all currently known Modules
	 */
	public synchronized Map<UUID, Module> getMap() {
		return new HashMap<UUID, Module>(map);
	}

	/**
	 * Updates information on modules, in explicit how the BlockTypeHolder has changed and which applications are
	 * running on the module.
	 * @return 
	 */
	public synchronized FutureNotifier<Collection<Module>> updateModuleInfo() {
		final Collection<FutureNotifier<? extends Module>> futures = new ArrayList<FutureNotifier<? extends Module>>();
		for (UUID uuid : map.keySet()){
			final FutureNotifier<Module> future = query.getModuleInfo(uuid);
			future.addListener(this);
			futures.add(future);
		}
		return new JoinedFutureNotifier<Module>(futures);
	}
	
	
}
