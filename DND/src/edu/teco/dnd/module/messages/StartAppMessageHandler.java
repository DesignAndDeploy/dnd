package edu.teco.dnd.module.messages;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.MessageHandler;

public class StartAppMessageHandler implements MessageHandler<StartAppMessage>{
	final ModuleApplicationManager appManager;
	public StartAppMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;
		
	}

	@Override
	public void handleMessage(UUID remoteUUID, StartAppMessage message) {
		appManager.startApplication(message.appId, remoteUUID, message.name);
	}

}
