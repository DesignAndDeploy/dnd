package edu.teco.dnd.module.messages.joinStartApp;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleID;
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
	private final Module module;

	/**
	 * 
	 * @param module
	 *            the Module to start the application on.
	 */
	public StartApplicationMessageHandler(Module module) {
		this.module = module;
	}

	@Override
	public Response handleMessage(ModuleID remoteID, StartApplicationMessage message) {
		try {
			module.startApp(message.getApplicationID());
		} catch (IllegalArgumentException e) {
			return new StartApplicationNak(message.getApplicationID());
		}
		return new StartApplicationAck(message.getApplicationID());
	}

}
