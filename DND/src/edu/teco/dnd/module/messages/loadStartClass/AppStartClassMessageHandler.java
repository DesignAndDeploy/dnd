package edu.teco.dnd.module.messages.loadStartClass;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class AppStartClassMessageHandler implements MessageHandler<AppStartClassMessage> {
	final ModuleApplicationManager appManager;

	public AppStartClassMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;
	}

	@Override
	public void handleMessage(ConnectionManager connectionManager, UUID remoteUUID, AppStartClassMessage message) {
		//TODO whatever deserializes this message probably wants the apps classloader!
		appManager.startBlock(message.getApplicationID(), message.getFunctionBlock());

	}

}
