package edu.teco.dnd.discover;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationInformation {
	private final UUID appId;
	private final String name;
	// private final Map<UUID/* BlockID */, UUID/* OnModuleID */> blocksRunningOn = new ConcurrentHashMap<UUID, UUID>();
	private final Map<UUID/* ModuleId */, Collection<UUID>/* BlockId */> modulesRunningBlocks =
			new ConcurrentHashMap<UUID, Collection<UUID>>();
	private Map<UUID, String> blockToType = new HashMap<UUID, String>();
	private Map<UUID, String> blockToName = new HashMap<UUID, String>();

	public ApplicationInformation(UUID appId, String name) {
		this.appId = appId;
		this.name = name;
	}

	public Collection<UUID> getModules() {
		return modulesRunningBlocks.keySet();
	}

	public void addBlockModulePair(UUID blockId, UUID moduleId) {
		Collection<UUID> blocks = modulesRunningBlocks.get(moduleId);
		if (blocks == null) {
			blocks = new HashSet<UUID>();
			modulesRunningBlocks.put(moduleId, blocks);
		}
		blocks.add(blockId);
	}

	public void addUUIDBlockTypePair(UUID blockID, String blockType) {
		blockToType.put(blockID, blockType);
	}

	public void addBlockIDNamePair(UUID blockUUID, String blockName) {
		blockToName.put(blockUUID, blockName);
	}

	public void addAllBlockModulePairs(Map<UUID/* blockID */, UUID/* moduleId */> blockModuleMapping) {
		for (Map.Entry<UUID, UUID> entr : blockModuleMapping.entrySet()) {
			addBlockModulePair(entr.getKey(), entr.getValue());
		}
	}

	/**
	 * @return the application Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of a FunctionBlock.
	 * 
	 * @param blockUUID
	 *            UUID of the function block.
	 * @return type of the function block.
	 */
	public String getBlockType(final UUID blockUUID) {
		return blockToType.get(blockUUID);
	}

	/**
	 * Returns the name the function block with the given UUID has within this application.
	 * 
	 * @param blockUUID
	 *            UUID of the function block. Although there may be several blocks with the same UUID on the modules,
	 *            within one application the UUID of a function block is unique.
	 * @return the Name the function block has in this application.
	 */
	public String getBlockName(final UUID blockUUID) {
		return blockToName.get(blockUUID);
	}

	/**
	 * @return the appId
	 */
	public UUID getAppId() {
		return appId;
	}

	/**
	 * @return which modules run which blocks. Map<module, collection<Blocks>>.
	 */
	public Map<UUID/* ModuleId */, Collection<UUID>/* BlockIds */> getBlocksRunningOn() {
		return modulesRunningBlocks;
	}

}
