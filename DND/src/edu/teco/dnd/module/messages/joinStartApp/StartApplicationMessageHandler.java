package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

public class StartApplicationMessageHandler implements MessageHandler<StartApplicationMessage> {
	final ModuleApplicationManager appManager;

	public StartApplicationMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;
	}

	@Override
	public Response handleMessage(ConnectionManager connectionManager, UUID remoteUUID, StartApplicationMessage message) {
		try {
			appManager.startApp(message.getApplicationID());
		} catch (IllegalArgumentException e) {
			return new StartApplicationNak(message);
		}
		return new StartApplicationAck(message);
	}

}
