package edu.teco.dnd.module.messages.killApp;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * triggers stopping the appropriate application upon receipt.
 * 
 * @author Marvin Marx
 * 
 */
public class KillAppMessageHandler implements MessageHandler<KillAppMessage> {
	/**
	 * the ApplicationManager to trigger the stopping on.
	 */
	private final ModuleApplicationManager appManager;

	/**
	 * 
	 * @param appManager
	 *            the ApplicationManager to trigger the stopping on.
	 */
	public KillAppMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;

	}

	@Override
	public Response handleMessage(ConnectionManager connMan, UUID remoteUUID, KillAppMessage message) {
		try {
			appManager.stopApplication(message.getApplicationID());
		} catch (Exception e) {
			return new KillAppNak(message.getApplicationID());

		}
		return new KillAppAck(message.getApplicationID());
	}

}
