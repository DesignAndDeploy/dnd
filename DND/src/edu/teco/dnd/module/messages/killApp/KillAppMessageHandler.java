package edu.teco.dnd.module.messages.killApp;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

public class KillAppMessageHandler implements MessageHandler<KillAppMessage> {
	final ModuleApplicationManager appManager;

	public KillAppMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;

	}

	@Override
	public Response handleMessage(ConnectionManager connMan, UUID remoteUUID, KillAppMessage message) {
		try {
			appManager.stopApplication(message.getApplicationID());
		} catch (Exception e) {
			return new KillAppNak(message.getApplicationID());

		}
		return new KillAppAck(message.getApplicationID());
	}

}
