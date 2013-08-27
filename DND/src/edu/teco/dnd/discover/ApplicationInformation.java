package edu.teco.dnd.discover;

import java.util.Collection;
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
