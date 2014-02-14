package edu.teco.dnd.module;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.messages.infoReq.ApplicationBlockID;

/**
 * Used to manage what BlockTypeHolders are used by running blocks. Provides a way to later remove a block from its
 * BlockTypeHolder.
 * 
 * @author Philipp Adolf
 */
public class ModuleBlockManager {
	private static final Logger LOGGER = LogManager.getLogger(ModuleBlockManager.class);

	private final Map<Integer, BlockTypeHolder> blockTypeHoldersByID;
	private final Map<ApplicationBlockID, Integer> spotOccupiedByBlock = new HashMap<ApplicationBlockID, Integer>();

	public ModuleBlockManager(final Map<Integer, BlockTypeHolder> blockTypeHoldersByID) {
		this.blockTypeHoldersByID = blockTypeHoldersByID;
	}

	/**
	 * Tries to add a FunctionBlock to a BlockTypeHolder.
	 * 
	 * @param applicationID
	 *            the ID of the application that is trying to add the block
	 * @param block
	 *            the block that should be added
	 * @param blockTypeHolderId
	 *            the ID of the BlockTypeHolder the block should be added to
	 * @throws NoSuchBlockTypeHolderException
	 *             if there is no BlockTypeHolder with the given ID
	 * @throws BlockTypeHolderFullException
	 *             if the BlockTypeHolder is already full
	 */
	public void addToBlockTypeHolders(final ApplicationID applicationID, final FunctionBlockSecurityDecorator block,
			final int blockTypeHolderId) throws BlockTypeHolderFullException, NoSuchBlockTypeHolderException {
		LOGGER.entry(applicationID, block, blockTypeHolderId);
		final BlockTypeHolder holder = blockTypeHoldersByID.get(blockTypeHolderId);
		if (holder == null) {
			throw LOGGER.throwing(new NoSuchBlockTypeHolderException("There is no BlockTypeHolder with ID "
					+ blockTypeHolderId));
		}
		synchronized (this) {
			if (!holder.tryAdd(block.getBlockType())) {
				throw LOGGER.throwing(new BlockTypeHolderFullException(holder + " is already full"));
			}
			spotOccupiedByBlock.put(new ApplicationBlockID(block.getBlockID(), applicationID), blockTypeHolderId);
		}
		LOGGER.exit();
	}

	/**
	 * Removes a FunctionBlock from the BlockTypeHolder it is occupying.
	 * 
	 * @param applicationBlockID
	 *            the ID for the block
	 */
	public void removeBlock(final ApplicationBlockID applicationBlockID) {
		LOGGER.entry(applicationBlockID);
		synchronized (this) {
			final BlockTypeHolder holder = blockTypeHoldersByID.get(spotOccupiedByBlock.remove(applicationBlockID));
			if (holder == null) {
				LOGGER.warn("did not find BlockTypeHolder for {}", applicationBlockID);
			} else {
				holder.increase();
			}
		}
		LOGGER.exit();
	}

	public static final class NoSuchBlockTypeHolderException extends Exception {
		private static final long serialVersionUID = -7492354487660781502L;

		private NoSuchBlockTypeHolderException(final String msg) {
			super(msg);
		}
	}

	public static final class BlockTypeHolderFullException extends Exception {
		private static final long serialVersionUID = -9057799390512391446L;

		private BlockTypeHolderFullException(final String msg) {
			super(msg);
		}
	}
}
