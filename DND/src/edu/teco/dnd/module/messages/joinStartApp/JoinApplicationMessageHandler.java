package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class JoinApplicationMessageHandler implements MessageHandler<JoinApplicationMessage> {
	final ModuleApplicationManager appManager;

	public JoinApplicationMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;

	}

	@Override
	public void handleMessage(ConnectionManager connMan, UUID remoteUUID, JoinApplicationMessage message) {
		try {
			appManager.startApplication(message.appId, remoteUUID, message.name);
		} catch (Exception e) {
			connMan.sendMessage(remoteUUID, new JoinApplicationNak(message.name, message.appId));
			return;
		}
		connMan.sendMessage(remoteUUID, new JoinApplicationAck(message.name, message.appId));
	}

}
