package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class WhoHasFuncBlockHandler implements MessageHandler<WhoHasBlockMessage> {
	private transient static final Logger LOGGER = LogManager.getLogger(WhoHasFuncBlockHandler.class);
	public final Application app;
	public final UUID ownModUuid;

	public WhoHasFuncBlockHandler(Application app, UUID ownModUuid) {
		this.app = app;
		this.ownModUuid = ownModUuid;
	}

	@Override
	public void handleMessage(ConnectionManager connMan, UUID remoteUUID, WhoHasBlockMessage message) {
		if (app.isExecuting(message.blockId)) {
			connMan.sendMessage(remoteUUID, new BlockFoundMessage(message.getApplicationID(), ownModUuid, message.blockId));
			// TODO register
		} else {
			LOGGER.trace("received who has msg for {}", message.blockId);
		}

	}

}
