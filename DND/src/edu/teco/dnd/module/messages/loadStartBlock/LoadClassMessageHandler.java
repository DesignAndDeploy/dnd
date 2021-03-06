package edu.teco.dnd.module.messages.loadStartBlock;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * triggers loading of a class into the classloader of an application.
 * 
 * @author Marvin Marx
 * 
 */
public class LoadClassMessageHandler implements MessageHandler<LoadClassMessage> {
	/**
	 * The application any arriving bytecode should be loaded into.
	 */
	private final Application associatedApp;

	/**
	 * 
	 * @param associatedApp
	 *            the application any arriving bytecode should be loaded into.
	 */
	public LoadClassMessageHandler(Application associatedApp) {
		this.associatedApp = associatedApp;

	}

	@Override
	public Response handleMessage(ModuleID remoteUUID, LoadClassMessage message) {
		try {
			associatedApp.loadClass(message.className, message.classByteCode);
		} catch (Exception e) {
			return new LoadClassNak(message.className);
		}
		return new LoadClassAck(message.className);
	}

}
