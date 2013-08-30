package edu.teco.dnd.server;

import java.util.ArrayList;
import java.util.Collection;
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

public class ApplicationManager implements FutureListener<FutureNotifier<Map<UUID, ApplicationInformation>>>, DNDServerStateListener {

	private ApplicationQuery query;
	private final Set<ApplicationManagerListener> listeners = new HashSet<ApplicationManagerListener>();
	private Collection<ApplicationInformation> apps = new ArrayList<ApplicationInformation>();

	protected ApplicationManager() {
	}

	public synchronized JoinedFutureNotifier<Map<UUID, ApplicationInformation>> updateAppInfo() {
		query = new ApplicationQuery(ServerManager.getDefault().getConnectionManager());
		final Collection<FutureNotifier<? extends Map<UUID, ApplicationInformation>>> futures =
				new ArrayList<FutureNotifier<? extends Map<UUID, ApplicationInformation>>>();
			final FutureNotifier<Map<UUID, ApplicationInformation>> future = query.getApplicationList();
			future.addListener(this);
			futures.add(future);
		return new JoinedFutureNotifier<Map<UUID,ApplicationInformation>>(futures);
	}

	public void addApplicationListener(ApplicationManagerListener listener){
		listeners.add(listener);
	}
	
	public void removeApplicationListener(ApplicationManagerListener listener){
		listeners.remove(listener);
	}
	
	
	@Override
	public void operationComplete(FutureNotifier<Map<UUID, ApplicationInformation>> future) throws Exception {
		if (future.isSuccess()) {
			Map<UUID, ApplicationInformation> map = future.getNow();
			for (UUID id : map.keySet()){
				apps.add(map.get(id));
			}
		}
		for (ApplicationManagerListener listener : listeners){
			listener.applicationsResolved(apps);
		}
	}

	@Override
	public void serverStarted(ConnectionManager connectionManager, UDPMulticastBeacon beacon) {
		apps.clear();
		query = new ApplicationQuery(connectionManager);
		for (ApplicationManagerListener listener : listeners){
			listener.serverOnline();
		}
	}

	@Override
	public void serverStopped() {
		apps.clear();
		for (ApplicationManagerListener listener : listeners){
			listener.serverOffline();
		}
		
	}

}
