package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class AppWhoHasFuncBlockHandler implements MessageHandler<AppWhoHasFuncBlockMessage> {
	private transient static final Logger LOGGER = LogManager.getLogger(AppWhoHasFuncBlockHandler.class);
	public final Application app;
	public final UUID ownModUuid;

	public AppWhoHasFuncBlockHandler(Application app, UUID ownModUuid) {
		this.app = app;
		this.ownModUuid = ownModUuid;
	}

	@Override
	public void handleMessage(ConnectionManager connMan, UUID remoteUUID, AppWhoHasFuncBlockMessage message) {
		if (app.isExecuting(message.funcBlock)) {
			connMan.sendMessage(remoteUUID, new AppBlockIdFoundMessage(message.appId, ownModUuid, message.funcBlock));
			// TODO register
		} else {
			LOGGER.trace("received who has msg for {}", message.funcBlock);
		}

	}

}
