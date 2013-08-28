package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
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
	private final ModuleApplicationManager appManager;

	/**
	 * 
	 * @param appManager
	 *            the AppManager to issue the join on.
	 */
	public JoinApplicationMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;

	}

	@Override
	public Response handleMessage(ConnectionManager connMan, UUID remoteUUID, JoinApplicationMessage message) {
		try {
			appManager.joinApplication(message.appId, remoteUUID, message.name);
		} catch (Exception e) {
			return new JoinApplicationNak(message.name, message.appId);
		}
		return new JoinApplicationAck(message.name, message.appId);
	}

}
