package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class ModInfoReqMsgHandler implements MessageHandler<ModInfoRequestMessage> {
	private final ConfigReader conf;
	private final ModuleApplicationManager appManager;

	public ModInfoReqMsgHandler(ConfigReader conf, ModuleApplicationManager appManager) {
		this.conf = conf;
		this.appManager = appManager;
	}

	@Override
	public void handleMessage(ConnectionManager connectionManager, UUID remoteUUID, ModInfoRequestMessage message) {
		connectionManager.sendMessage(remoteUUID, new ModuleInfoMessage(conf, appManager));
	}

}
