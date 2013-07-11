package edu.teco.dnd.discover;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import edu.teco.dnd.module.messages.infoReq.RequestApplicationListMessage;
import edu.teco.dnd.module.messages.infoReq.ApplicationListResponse;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

public class ApplicationQuery {
	private final ConnectionManager connectionManager;
	
	private final RequestApplicationListMessage request;
	
	public ApplicationQuery(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
		this.request = new RequestApplicationListMessage();
	}
	
	public ModuleApplicationListFutureNotifier getApplicationList(final UUID module) {
		final ModuleApplicationListFutureNotifier notifier = new ModuleApplicationListFutureNotifier(module);
		connectionManager.sendMessage(module, request).addListener(notifier);
		return notifier;
	}
	
	public ApplicationListFutureNotifier getApplicationList() {
		final Collection<UUID> modules = connectionManager.getConnectedModules();
		final ApplicationListFutureNotifier notifier = new ApplicationListFutureNotifier(modules.size());
		for (final UUID module : modules) {
			connectionManager.sendMessage(module, request).addListener(notifier);
		}
		return notifier;
	}
	
	public static class ModuleApplicationListFutureNotifier extends DefaultFutureNotifier<Map<UUID, String>>
			implements FutureListener<FutureNotifier<Response>> {
		private final UUID moduleUUID;
		
		public ModuleApplicationListFutureNotifier(final UUID moduleUUID) {
			this.moduleUUID = moduleUUID;
		}
		
		public UUID getModuleUUID() {
			return this.moduleUUID;
		}
		
		@Override
		public void operationComplete(FutureNotifier<Response> future) {
			if (future.isSuccess()) {
				final Response response = future.getNow();
				if (response == null || !(response instanceof ApplicationListResponse)) {
					setFailure(new IllegalArgumentException("did not get an ApplicationListResponse"));
				} else {
					setSuccess(((ApplicationListResponse) response).getApplications());
				}
			} else {
				setFailure(future.cause());
			}
		}
	}
	
	public static class ApplicationListFutureNotifier extends DefaultFutureNotifier<Map<UUID, Map<UUID, String>>>
			implements FutureListener<FutureNotifier<Response>> {
		private final AtomicInteger waiting;
		
		private final ConcurrentMap<UUID, Map<UUID, String>> applications;
		
		public ApplicationListFutureNotifier(final int waiting) {
			if (waiting == 0) {
				this.waiting = null;
				this.applications = null;
				setSuccess(new HashMap<UUID, Map<UUID,String>>());
			} else {
				this.waiting = new AtomicInteger(waiting);
				this.applications = new ConcurrentHashMap<UUID, Map<UUID,String>>();
			}
		}

		@Override
		public void operationComplete(final FutureNotifier<Response> future) {
			if (future.isSuccess()) {
				final Response response = future.getNow();
				if (response instanceof ApplicationListResponse) {
					final ApplicationListResponse applicationListResponse = (ApplicationListResponse) response;
					applications.put(applicationListResponse.getModuleUUID(),
							applicationListResponse.getApplications());
				}
			}
			if (waiting.decrementAndGet() <= 0) {
				setSuccess(applications);
			}
		}
	}
}
