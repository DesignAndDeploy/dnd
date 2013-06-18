package edu.teco.dnd.module.messages.startApp;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class StartAppMessageHandler implements MessageHandler<StartAppMessage> {
	final ModuleApplicationManager appManager;

	public StartAppMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;

	}

	@Override
	public void handleMessage(ConnectionManager connMan, UUID remoteUUID, StartAppMessage message) {
		try {
			appManager.startApplication(message.appId, remoteUUID, message.name);
		} catch (Exception e) {
			connMan.sendMessage(remoteUUID, new StartAppNak(message.name, message.appId));
			return;
		}
		connMan.sendMessage(remoteUUID, new StartAppAck(message.name, message.appId));
	}

}
