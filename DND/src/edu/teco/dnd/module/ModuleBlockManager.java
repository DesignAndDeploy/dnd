package edu.teco.dnd.module;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.messages.infoReq.ApplicationBlockID;

/**
 * Manages the {@link BlockTypeHolder}s of a {@link Module}. Each Module has one ModuleBlockManager that is used to use
 * and free BlockTypeHolders in a thread-safe way.
 */
public class ModuleBlockManager {
	private static final Logger LOGGER = LogManager.getLogger(ModuleBlockManager.class);

	private final Map<Integer, BlockTypeHolder> blockTypeHoldersByID;
	private final Map<ApplicationBlockID, Integer> spotOccupiedByBlock = new HashMap<ApplicationBlockID, Integer>();

	/**
	 * Initializes a new ModuleBlockManager.
	 * 
	 * @param the
	 *            root of the {@link BlockTypeHolder} tree used by the {@link Module}
	 */
	public ModuleBlockManager(final BlockTypeHolder rootHolder) {
		this.blockTypeHoldersByID = Collections.unmodifiableMap(createBlockTypeHolderMap(rootHolder));
	}

	private Map<Integer, BlockTypeHolder> createBlockTypeHolderMap(final BlockTypeHolder rootHolder) {
		final Map<Integer, BlockTypeHolder> map = new HashMap<Integer, BlockTypeHolder>();
		for (final BlockTypeHolder current : rootHolder) {
			map.put(current.getID(), current);
		}
		return map;
	}

	/**
	 * Tries to add a {@link FunctionBlock} to a BlockTypeHolder.
	 * 
	 * @param applicationID
	 *            the ID of the {@link Application} that is trying to add the block
	 * @param block
	 *            the {@link FunctionBlockSecurityDecorator} for the FunctionBlock that should be added
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
