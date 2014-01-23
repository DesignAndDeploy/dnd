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
import edu.teco.dnd.module.messages.generalModule.MissingApplicationNak;
import edu.teco.dnd.module.messages.killApp.KillAppAck;
import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.module.messages.killApp.KillAppNak;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;
import edu.teco.dnd.util.JoinedFutureNotifier;

public class ApplicationManager implements FutureListener<FutureNotifier<Map<UUID, ApplicationInformation>>>,
		ServerStateListener {

	private ApplicationQuery query;
	private final Set<ApplicationManagerListener> listeners = new HashSet<ApplicationManagerListener>();
	private Collection<ApplicationInformation> apps = new ArrayList<ApplicationInformation>();
	private Map<UUID, ApplicationInformation> idToApp = new HashMap<UUID, ApplicationInformation>();
	
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

	/**
	 * Returns a list of currently running Applications, represented by ApplicationInformation.
	 * @return List of currently running applications.
	 */
	public Collection<ApplicationInformation> getApps(){
		return apps;
	}
	
	/**
	 * Returns a map that tells you which application belongs to which UUID.
	 * @return Map from UUID of an application to the ApplicationInformation for the application.
	 */
	public Map<UUID, ApplicationInformation> getMap(){
		return idToApp;
	}
	
	public FutureNotifier<Void> killApplication(final UUID applicationID) {
		final ApplicationInformation applicationInformation = idToApp.get(applicationID);
		if (applicationInformation == null) {
			throw new IllegalArgumentException("Application " + applicationID + " unknown");
		}

		final ConnectionManager connectionManager = ServerManager.getDefault().getConnectionManager();
		if (connectionManager == null) {
			throw new IllegalStateException("server not running");
		}

		final Collection<FutureNotifier<? extends Response>> futures =
				new ArrayList<FutureNotifier<? extends Response>>();
		for (final UUID moduleID : applicationInformation.getModules()) {
			final KillAppMessage killAppMessage = new KillAppMessage(applicationID);
			futures.add(connectionManager.sendMessage(moduleID, killAppMessage));
		}
		final JoinedFutureNotifier<Response> joinedFutureNotifier = new JoinedFutureNotifier<Response>(futures);
		final KillAppFutureNotifier notifier = new KillAppFutureNotifier();
		joinedFutureNotifier.addListener(notifier);
		return notifier;
	}
	
	@Override
	public void operationComplete(FutureNotifier<Map<UUID, ApplicationInformation>> future) throws Exception {
		if (future.isSuccess()) {
			idToApp = future.getNow();
			for (ApplicationInformation app : idToApp.values()){
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
	public void serverStateChanged(ServerState state, ConnectionManager connectionManager, UDPMulticastBeacon beacon) {
		switch (state) {
		case RUNNING:
			serverStarted(connectionManager);
			break;
			
		case STOPPED:
			serverStopped();
		}
	}

	private void serverStarted(ConnectionManager connectionManager) {
		apps.clear();
		query = new ApplicationQuery(connectionManager);
		for (ApplicationManagerListener listener : listeners) {
			listener.serverOnline();
		}
	}

	private void serverStopped() {
		apps.clear();
		for (ApplicationManagerListener listener : listeners) {
			listener.serverOffline();
		}

	}

	private static class KillAppFutureNotifier extends DefaultFutureNotifier<Void> implements
			FutureListener<FutureNotifier<Collection<Response>>> {
		@Override
		public void operationComplete(final FutureNotifier<Collection<Response>> future) {
			if (future.isSuccess()) {
				for (final Response response : future.getNow()) {
					if (response instanceof KillAppAck || response instanceof MissingApplicationNak) {
						continue;
					} else if (response instanceof KillAppNak) {
						setFailure(new Exception("got negative acknowledgment"));
						return;
					} else {
						setFailure(null);
						return;
					}
				}

				setSuccess(null);
			} else {
				setFailure(future.cause());
			}
		}
	}
}
