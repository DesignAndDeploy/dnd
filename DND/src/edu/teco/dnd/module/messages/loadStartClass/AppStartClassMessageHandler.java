package edu.teco.dnd.module.messages.loadStartClass;

import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class AppStartClassMessageHandler implements MessageHandler<AppStartClassMessage> {
	final ModuleApplicationManager appManager;
	final Application associatedApp;

	public AppStartClassMessageHandler(ModuleApplicationManager appManager, Application associatedApp) {
		this.appManager = appManager;
		this.associatedApp = associatedApp;
	}

	@Override
	public void handleMessage(ConnectionManager connectionManager, UUID remoteUUID, AppStartClassMessage message) {
		associatedApp.startBlock(message.getFunctionBlock());
		
	}

}
