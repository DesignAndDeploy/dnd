package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class LoadClassMessageHandler implements MessageHandler<LoadClassMessage> {
	final ModuleApplicationManager appManager;
	final Application associatedApp;

	public LoadClassMessageHandler(ModuleApplicationManager appManager, Application associatedApp) {
		this.appManager = appManager;
		this.associatedApp = associatedApp;

	}

	@Override
	public void handleMessage(ConnectionManager connMan, UUID remoteUUID, LoadClassMessage message) {
		try {
			associatedApp.loadClass(message.className, message.classByteCode);
		} catch (Exception e) {
			connMan.sendMessage(remoteUUID, new LoadClassNak(message.className, message.getApplicationID()));
			return;
		}
		connMan.sendMessage(remoteUUID, new LoadClassAck(message.className, message.getApplicationID()));
	}

}
