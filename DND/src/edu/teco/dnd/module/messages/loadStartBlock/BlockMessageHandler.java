package edu.teco.dnd.module.messages.loadStartBlock;

import edu.teco.dnd.module.BlockDescription;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleBlockManager.BlockTypeHolderFullException;
import edu.teco.dnd.module.ModuleBlockManager.NoSuchBlockTypeHolderException;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.module.UserSuppliedCodeException;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Triggers scheduling a block into a given application.
 * 
 */
public class BlockMessageHandler implements MessageHandler<BlockMessage> {
	/**
	 * Module to trigger the scheduling on.
	 */
	private final Module module;

	/**
	 * 
	 * @param module
	 *            Module to trigger the scheduling on.
	 */
	public BlockMessageHandler(Module module) {
		this.module = module;
	}

	@Override
	public Response handleMessage(ModuleID remoteID, BlockMessage message) {
		final BlockDescription blockDescription =
				new BlockDescription(message.blockClass, message.blockName, message.blockUUID, message.options,
						message.outputs, message.scheduleToId);
		try {
			module.scheduleBlock(message.getApplicationID(), blockDescription);
		} catch (final ClassNotFoundException e) {
			return new BlockNak(e);
		} catch (final UserSuppliedCodeException e) {
			return new BlockNak(e);
		} catch (final IllegalArgumentException e) {
			return new BlockNak(e);
		} catch (BlockTypeHolderFullException e) {
			return new BlockNak(e);
		} catch (NoSuchBlockTypeHolderException e) {
			return new BlockNak(e);
		}
		return new BlockAck();
	}
}
