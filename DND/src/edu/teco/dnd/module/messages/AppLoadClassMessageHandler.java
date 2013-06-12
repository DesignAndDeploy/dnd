package edu.teco.dnd.module.messages;

import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.MessageHandler;

public class AppLoadClassMessageHandler  implements MessageHandler<AppLoadClassMessage> {
	final ModuleApplicationManager appManager;
	final Application associatedApp;
	
	public AppLoadClassMessageHandler(ModuleApplicationManager appManager, Application associatedApp) {
		this.associatedApp = associatedApp;
		this.appManager = appManager;
		
	}

	@Override
	public void handleMessage(UUID remoteUUID, AppLoadClassMessage message) {
		//TODO
	}

}
