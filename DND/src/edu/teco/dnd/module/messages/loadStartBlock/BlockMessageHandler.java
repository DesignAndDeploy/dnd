package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Triggers scheduling a block into a given application.
 * 
 */
public class BlockMessageHandler implements MessageHandler<BlockMessage> {
	/**
	 * ApplicationManager to trigger the scheduling on.
	 */
	private final ModuleApplicationManager appManager;

	/**
	 * 
	 * @param appManager
	 *            ApplicationManager to trigger the scheduling on.
	 */
	public BlockMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;
	}

	@Override
	public Response handleMessage(ConnectionManager connMan, UUID remoteUUID, BlockMessage message) {
		if (appManager.scheduleBlock(message.getApplicationID(), message.blockClass, message.blockUUID,
				message.options, message.outputs, message.scheduleToId)) {
			return new BlockAck();
		} else {
			return new BlockNak();
		}
	}
}
