package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

public class RequestModuleInfoMsgHandler implements MessageHandler<RequestModuleInfoMessage> {
	private final ConfigReader conf;
	private final ModuleApplicationManager appManager;

	public RequestModuleInfoMsgHandler(ConfigReader conf, ModuleApplicationManager appManager) {
		this.conf = conf;
		this.appManager = appManager;
	}

	@Override
	public Response handleMessage(ConnectionManager connectionManager, UUID remoteUUID, RequestModuleInfoMessage message) {
		return new ModuleInfoMessage(conf, appManager);
	}

}
