package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.module.BlockDescription;
import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.module.UserSuppliedCodeException;
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
	public Response handleMessage(UUID remoteUUID, BlockMessage message) {
		final BlockDescription blockDescription = new BlockDescription(message.blockClass, message.blockName, message.blockUUID, message.options, message.outputs, message.scheduleToId);
		try {
			appManager.scheduleBlock(message.getApplicationID(), blockDescription);
		} catch (final ClassNotFoundException e) {
			return new BlockNak(e);
		} catch (final UserSuppliedCodeException e) {
			return new BlockNak(e);
		} catch (final IllegalArgumentException e) {
			return new BlockNak(e);
		}
		return new BlockAck();
	}
}
