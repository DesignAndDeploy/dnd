package edu.teco.dnd.module.messages.joinStartApp;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Handle JoinApp messages. Triggers Joining the application on the given ApplicationManager.
 * 
 * @author Marvin Marx
 * 
 */
public class JoinApplicationMessageHandler implements MessageHandler<JoinApplicationMessage> {
	/**
	 * ApplicationManager to issue the join on.
	 */
	private final Module module;

	/**
	 * 
	 * @param module
	 *            the Module to issue the join on.
	 */
	public JoinApplicationMessageHandler(Module module) {
		this.module = module;

	}

	@Override
	public Response handleMessage(ModuleID remoteID, JoinApplicationMessage message) {
		try {
			module.createNewApplication(message.applicationID, message.name);
		} catch (Exception e) {
			return new JoinApplicationNak(message.name, message.applicationID);
		}
		return new JoinApplicationAck(message.name, message.applicationID);
	}

}
