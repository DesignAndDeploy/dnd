package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Triggers a check whether this application on this module is executing a given FunctionBlock. If so this is positively
 * acknowledged (see blockFoundMessage). If not an empty reply is returned.
 * 
 * @author Marvin Marx
 * 
 */
public class WhoHasFuncBlockHandler implements MessageHandler<WhoHasBlockMessage> {
	private static final transient Logger LOGGER = LogManager.getLogger(WhoHasFuncBlockHandler.class);
	/**
	 * The app this is in regard to.
	 */
	private final Application app;
	/**
	 * The UUID of the module this is executed upon.
	 */
	private final UUID ownModUuid;

	/**
	 * 
	 * @param app
	 *            The app this is in regard to.
	 * @param ownModUuid
	 *            The moduleId this is executed upon. If block found obviously also the id that is returned as having
	 *            this block.
	 */
	public WhoHasFuncBlockHandler(Application app, UUID ownModUuid) {
		this.app = app;
		this.ownModUuid = ownModUuid;
	}

	@Override
	public Response handleMessage(ConnectionManager connMan, UUID remoteUUID, WhoHasBlockMessage message) {
		LOGGER.entry(connMan, remoteUUID, message);
		if (app.isExecuting(message.blockId)) {
			return new BlockFoundResponse(ownModUuid);
		} else {
			LOGGER.trace("received who has msg for {}", message.blockId);
			return null;
		}

	}

}
