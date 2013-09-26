package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Triggers starting of applications on the given ApplicationManager.
 * 
 * @author Marvin Marx
 * 
 */
public class StartApplicationMessageHandler implements MessageHandler<StartApplicationMessage> {
	/**
	 * ApplicationManager to start the Application on.
	 */
	private final ModuleApplicationManager appManager;

	/**
	 * 
	 * @param appManager
	 *            the applicationManager to start the application on.
	 */
	public StartApplicationMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;
	}

	@Override
	public Response handleMessage(UUID remoteUUID, StartApplicationMessage message) {
		try {
			appManager.startApp(message.getApplicationID());
		} catch (IllegalArgumentException e) {
			return new StartApplicationNak(message);
		}
		return new StartApplicationAck(message);
	}

}
