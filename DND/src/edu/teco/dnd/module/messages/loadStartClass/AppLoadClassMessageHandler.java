package edu.teco.dnd.module.messages.loadStartClass;

import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class AppLoadClassMessageHandler implements MessageHandler<AppLoadClassMessage> {
	final ModuleApplicationManager appManager;
	final Application associatedApp;

	public AppLoadClassMessageHandler(ModuleApplicationManager appManager, Application associatedApp) {
		this.appManager = appManager;
		this.associatedApp = associatedApp;

	}

	@Override
	public void handleMessage(ConnectionManager connMan, UUID remoteUUID, AppLoadClassMessage message) {
		try {
			associatedApp.loadClass(message.className, message.classByteCode);
		} catch (Exception e) {
			connMan.sendMessage(remoteUUID, new AppLoadClassNak(message.className, message.getApplicationID()));
			return;
		}
		connMan.sendMessage(remoteUUID, new AppLoadClassAck(message.className, message.getApplicationID()));
	}

}
