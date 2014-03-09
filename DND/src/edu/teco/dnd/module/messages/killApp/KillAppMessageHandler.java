package edu.teco.dnd.module.messages.killApp;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleID;
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
	 * the Module to trigger the stopping on.
	 */
	private final Module module;

	/**
	 * 
	 * @param module
	 *            the Module to trigger the stopping on.
	 */
	public KillAppMessageHandler(Module module) {
		this.module = module;

	}

	@Override
	public Response handleMessage(ModuleID remoteUUID, KillAppMessage message) {
		final Application application = module.getApplication(message.getApplicationID());
		if (application == null) {
			return new KillAppNak(message.getApplicationID());
		}

		try {
			application.shutdown();
		} catch (final IllegalStateException e) {
			return new KillAppNak(message.getApplicationID());
		}

		return new KillAppAck(message.getApplicationID());
	}

}
