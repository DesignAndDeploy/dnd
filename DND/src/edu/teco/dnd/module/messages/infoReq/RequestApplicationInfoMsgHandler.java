package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

public class RequestApplicationInfoMsgHandler implements MessageHandler<RequestApplicationInfoMessage> {
	private final Application app;

	public RequestApplicationInfoMsgHandler(Application app) {
		this.app  = app;
	}

	@Override
	public Response handleMessage(ConnectionManager connectionManager, UUID remoteUUID, RequestApplicationInfoMessage message) {
		return new ApplicationInfoMessage(app);
	}

}
