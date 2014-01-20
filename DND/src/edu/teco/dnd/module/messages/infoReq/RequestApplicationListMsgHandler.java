package edu.teco.dnd.module.messages.infoReq;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Handles {@link RequestApplicationListMessage} by sending an {@link ApplicationListResponse}.
 * 
 * @author Philipp Adolf
 */
public class RequestApplicationListMsgHandler implements MessageHandler<RequestApplicationListMessage> {
	private final UUID moduleUUID;
	private final Module module;

	/**
	 * create a new AppListMsgHandler.
	 * 
	 * @param moduleUUID
	 *            UUID of the module this is running on.
	 * @param module
	 *            the module this is running on. Needed to extract the running applications.
	 */
	public RequestApplicationListMsgHandler(final UUID moduleUUID, final Module module) {
		this.moduleUUID = moduleUUID;
		this.module = module;
	}

	@Override
	public Response handleMessage(final UUID remoteUUID,
			final RequestApplicationListMessage message) {
		final Map<UUID, String> applicationNames = new HashMap<UUID, String>();
		final Map<UUID, String> uuidToBlockType = new HashMap<UUID, String>();
		final Map<ApplicationBlockID, String> applicationBlockIDToBlockName = new HashMap<ApplicationBlockID, String>();
		final Map<UUID, Collection<UUID>> applicationBlocks = new HashMap<UUID, Collection<UUID>>();

		for (final Application application : module.getRunningApps().values()) {
			for (UUID id : application.getFunctionBlocksById().keySet()){
				uuidToBlockType.put(id, application.getFunctionBlocksById().get(id).getBlockType());
				applicationBlockIDToBlockName.put(new ApplicationBlockID(id, application.getApplicationID()), application.getFunctionBlocksById().get(id).getBlockName());
			}
			applicationNames.put(application.getApplicationID(), application.getName());
			applicationBlocks.put(application.getApplicationID(), application.getFunctionBlocksById().keySet());
		}
		return new ApplicationListResponse(moduleUUID, applicationNames, applicationBlocks, uuidToBlockType, applicationBlockIDToBlockName);
	}
}
