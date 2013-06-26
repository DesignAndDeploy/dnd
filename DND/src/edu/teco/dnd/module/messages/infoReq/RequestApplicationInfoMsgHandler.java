package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class RequestApplicationInfoMsgHandler implements MessageHandler<RequestApplicationInfoMessage> {
	private final Application app;

	public RequestApplicationInfoMsgHandler(Application app) {
		this.app  = app;
	}

	@Override
	public void handleMessage(ConnectionManager connectionManager, UUID remoteUUID, RequestApplicationInfoMessage message) {
		connectionManager.sendMessage(remoteUUID, new ApplicationInfoMessage(app));
	}

}
