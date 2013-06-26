package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class StartApplicationMessageHandler implements MessageHandler<StartApplicationMessage> {
	final ModuleApplicationManager appManager;

	public StartApplicationMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;
	}

	@Override
	public void handleMessage(ConnectionManager connectionManager, UUID remoteUUID, StartApplicationMessage message) {
		appManager.startApp(message.appId);
	}

}
