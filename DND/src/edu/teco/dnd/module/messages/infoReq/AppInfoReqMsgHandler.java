package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class AppInfoReqMsgHandler implements MessageHandler<AppInfoRequestMessage> {
	private final Application app;

	public AppInfoReqMsgHandler(Application app) {
		this.app  = app;
	}

	@Override
	public void handleMessage(ConnectionManager connectionManager, UUID remoteUUID, AppInfoRequestMessage message) {
		connectionManager.sendMessage(remoteUUID, new ApplicationInfoMessage(app));
	}

}
