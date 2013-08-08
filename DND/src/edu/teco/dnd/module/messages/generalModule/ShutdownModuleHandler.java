package edu.teco.dnd.module.messages.generalModule;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

public class ShutdownModuleHandler implements MessageHandler<ShutdownModuleMessage> {
	private final ModuleApplicationManager appMan;

	public ShutdownModuleHandler(ModuleApplicationManager appMan) {
		this.appMan = appMan;
	}

	@Override
	public Response handleMessage(ConnectionManager connectionManager, UUID remoteUUID, ShutdownModuleMessage message) {
		try {
			appMan.shutdownModule();
		} catch (Exception ex) {
			return new ShutdownModuleNak();
		}
		return new ShutdownModuleAck();
	}

}
