package edu.teco.dnd.module.messages.killApp;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class KillAppMessageHandler implements MessageHandler<KillAppMessage> {
	final ModuleApplicationManager appManager;

	public KillAppMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;

	}

	//TODO register
	@Override
	public void handleMessage(ConnectionManager connMan, UUID remoteUUID, KillAppMessage message) {
		try {
			appManager.stopApplication(message.appId);
		} catch (Exception e) {
			connMan.sendMessage(remoteUUID, new KillAppNak(message.appId));
			return;
		}
		connMan.sendMessage(remoteUUID, new KillAppAck(message.appId));
	}

}
