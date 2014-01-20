package edu.teco.dnd.module.messages.generalModule;

import java.util.UUID;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Handler for Shutdown messages.
 * 
 * @author Marvin Marx
 * 
 */
public class ShutdownModuleHandler implements MessageHandler<ShutdownModuleMessage> {
	private final Module module;

	/**
	 * 
	 * @param module
	 *            the associated module. The instance the shutdown command will be passed on
	 *            to.
	 */
	public ShutdownModuleHandler(Module module) {
		this.module = module;
	}

	@Override
	public Response handleMessage(UUID remoteUUID, ShutdownModuleMessage message) {
		try {
			module.shutdownModule();
		} catch (Exception ex) {
			return new ShutdownModuleNak();
		}
		return new ShutdownModuleAck();
	}

}
