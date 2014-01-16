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
		final Map<BlockID, String> blockIDToBlockName = new HashMap<BlockID, String>();
		final Map<UUID, Collection<UUID>> applicationBlocks = new HashMap<UUID, Collection<UUID>>();

		for (final Application application : module.getRunningApps().values()) {
			for (UUID id : application.getFuncBlockById().keySet()){
				uuidToBlockType.put(id, application.getFuncBlockById().get(id).getBlockType());
				blockIDToBlockName.put(new BlockID(id, application.getOwnAppId()), application.getFuncBlockById().get(id).getBlockName());
			}
			applicationNames.put(application.getOwnAppId(), application.getName());
			applicationBlocks.put(application.getOwnAppId(), application.getFuncBlockById().keySet());
		}
		return new ApplicationListResponse(moduleUUID, applicationNames, applicationBlocks, uuidToBlockType, blockIDToBlockName);
	}
}
