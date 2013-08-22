package edu.teco.dnd.module.messages.infoReq;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Handles {@link RequestApplicationListMessage} by sending an {@link ApplicationListResponse}.
 * 
 * @author Philipp Adolf
 */
public class RequestApplicationListMsgHandler implements MessageHandler<RequestApplicationListMessage> {
	private final UUID moduleUUID;
	private final ModuleApplicationManager applicationManager;

	public RequestApplicationListMsgHandler(final UUID moduleUUID, final ModuleApplicationManager applicationManager) {
		this.moduleUUID = moduleUUID;
		this.applicationManager = applicationManager;
	}

	@Override
	public Response handleMessage(final ConnectionManager connectionManager, final UUID remoteUUID,
			final RequestApplicationListMessage message) {
		final Map<UUID, String> applicationNames = new HashMap<UUID, String>();
		final Map<UUID, Collection<UUID>> applicationBlocks = new HashMap<UUID, Collection<UUID>>();

		for (final Application application : applicationManager.getRunningApps().values()) {
			applicationNames.put(application.getOwnAppId(), application.getName());
			applicationBlocks.put(application.getOwnAppId(), application.getFuncBlockById().keySet());
		}
		return new ApplicationListResponse(moduleUUID, applicationNames, applicationBlocks);
	}
}
