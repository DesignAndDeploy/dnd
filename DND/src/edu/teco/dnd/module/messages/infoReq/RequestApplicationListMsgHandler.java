package edu.teco.dnd.module.messages.infoReq;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Handles {@link AppListRequestMessage} by sending an {@link ApplicationListResponse}.
 *
 * @author Philipp Adolf
 */
public class RequestApplicationListMsgHandler implements MessageHandler<AppListRequestMessage> {
	private final UUID moduleUUID;
	private final ModuleApplicationManager applicationManager;
	
	public RequestApplicationListMsgHandler(final UUID moduleUUID, final ModuleApplicationManager applicationManager) {
		this.moduleUUID = moduleUUID;
		this.applicationManager = applicationManager;
	}
	
	@Override
	public Response handleMessage(final ConnectionManager connectionManager, final  UUID remoteUUID,
			final AppListRequestMessage message) {
		final Map<UUID, String> applications = new HashMap<UUID, String>();
		for (final Application application : applicationManager.getRunningApps().values()) {
			applications.put(application.getOwnAppId(), application.getName());
		}
		return new ApplicationListResponse(moduleUUID, applications);
	}
}
