package edu.teco.dnd.module.messages.generalModule;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Handler for Shutdown messages.
 * 
 * @author Marvin Marx
 * 
 */
public class ShutdownModuleHandler implements MessageHandler<ShutdownModuleMessage> {
	private final ModuleApplicationManager appMan;

	/**
	 * 
	 * @param appMan
	 *            the applicationManager of the associated module. The instance the shutdown command will be passed on
	 *            to.
	 */
	public ShutdownModuleHandler(ModuleApplicationManager appMan) {
		this.appMan = appMan;
	}

	@Override
	public Response handleMessage(UUID remoteUUID, ShutdownModuleMessage message) {
		try {
			appMan.shutdownModule();
		} catch (Exception ex) {
			return new ShutdownModuleNak();
		}
		return new ShutdownModuleAck();
	}

}
