package edu.teco.dnd.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.teco.dnd.discover.ApplicationInformation;
import edu.teco.dnd.discover.ApplicationQuery;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;
import edu.teco.dnd.util.JoinedFutureNotifier;

public class ApplicationManager implements FutureListener<FutureNotifier<Map<UUID, ApplicationInformation>>>,
		DNDServerStateListener {

	private ApplicationQuery query;
	private final Set<ApplicationManagerListener> listeners = new HashSet<ApplicationManagerListener>();
	private Collection<ApplicationInformation> apps = new ArrayList<ApplicationInformation>();

	private Map<UUID, Collection<ApplicationInformation>> moduleToApp =
			new HashMap<UUID, Collection<ApplicationInformation>>();

	protected ApplicationManager() {
		ServerManager.getDefault().addServerStateListener(this);
	}

	public synchronized JoinedFutureNotifier<Map<UUID, ApplicationInformation>> updateAppInfo() {
		query = new ApplicationQuery(ServerManager.getDefault().getConnectionManager());
		apps.clear();
		moduleToApp.clear();
		final Collection<FutureNotifier<? extends Map<UUID, ApplicationInformation>>> futures =
				new ArrayList<FutureNotifier<? extends Map<UUID, ApplicationInformation>>>();
		final FutureNotifier<Map<UUID, ApplicationInformation>> future = query.getApplicationList();
		future.addListener(this);
		futures.add(future);
		return new JoinedFutureNotifier<Map<UUID, ApplicationInformation>>(futures);
	}

	public void addApplicationListener(ApplicationManagerListener listener) {
		listeners.add(listener);
	}

	public void removeApplicationListener(ApplicationManagerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Returns a Map from all modules to the Applications that are running on them. The modules are only identified by
	 * their UUIDs, but can be resolved by the {@link ModuleManager}.
	 * 
	 * @return Map from module to applications running on it.
	 */
	public Map<UUID, Collection<ApplicationInformation>> getModulesToApps() {
		return moduleToApp;
	}

	@Override
	public void operationComplete(FutureNotifier<Map<UUID, ApplicationInformation>> future) throws Exception {
		if (future.isSuccess()) {
			Map<UUID, ApplicationInformation> map = future.getNow();
			for (ApplicationInformation app : map.values()){
				apps.add(app);
				for (UUID id : app.getModules()){
					Collection<ApplicationInformation> info = moduleToApp.get(id);
					if (info == null || info.isEmpty()) {
						info = new ArrayList<ApplicationInformation>();
					}
					info.add(app);
					moduleToApp.put(id, info);
				}
			}
		}
		for (ApplicationManagerListener listener : listeners) {
			listener.applicationsResolved(apps);
		}
	}

	@Override
	public void serverStarted(ConnectionManager connectionManager, UDPMulticastBeacon beacon) {
		apps.clear();
		query = new ApplicationQuery(connectionManager);
		for (ApplicationManagerListener listener : listeners) {
			listener.serverOnline();
		}
	}

	@Override
	public void serverStopped() {
		apps.clear();
		for (ApplicationManagerListener listener : listeners) {
			listener.serverOffline();
		}

	}

}
