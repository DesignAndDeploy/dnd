package edu.teco.dnd.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import edu.teco.dnd.module.ApplicationID;

/**
 * Stores information about a single application.
 * 
 * @author Philipp Adolf
 */
public class ApplicationInformation {
	private final ApplicationID id;
	private final String name;
	private final Map<UUID, Collection<BlockInformation>> blocksByModules;

	/**
	 * Initializes a new ApplicationInformation.
	 * 
	 * @param id
	 *            the ID of the Application
	 * @param name
	 *            the name of the Application
	 * @param blocks
	 *            the FunctionBlocks belonging to the Application
	 */
	public ApplicationInformation(final ApplicationID id, final String name,
			final Collection<? extends BlockInformation> blocks) {
		this.id = id;
		this.name = name;

		final Map<UUID, Collection<BlockInformation>> blockMap = new HashMap<UUID, Collection<BlockInformation>>();
		for (final BlockInformation block : blocks) {
			if (block != null) {
				addToMapCollection(blockMap, block.getModuleID(), block);
			}
		}
		blocksByModules = unmodifiableMapCollection(blockMap);
	}

	private <K, V> Map<K, Collection<V>> unmodifiableMapCollection(Map<K, ? extends Collection<? extends V>> map) {
		final Map<K, Collection<V>> newMap = new HashMap<K, Collection<V>>();
		for (final Entry<K, ? extends Collection<? extends V>> entry : map.entrySet()) {
			newMap.put(entry.getKey(), Collections.unmodifiableCollection(entry.getValue()));
		}
		return Collections.unmodifiableMap(newMap);
	}

	private <K, V> void addToMapCollection(final Map<K, Collection<V>> map, final K key, final V value) {
		Collection<V> collection = map.get(key);
		if (collection == null) {
			collection = new ArrayList<V>();
			map.put(key, collection);
		}
		collection.add(value);
	}

	public ApplicationInformation combine(final ApplicationInformation other) {
		if (other == null) {
			throw new IllegalArgumentException("other must not be null");
		}
		if (!id.equals(other.id)) {
			throw new IllegalArgumentException("other Application has a different ID");
		}
		final Collection<BlockInformation> newBlocks = new ArrayList<BlockInformation>();
		newBlocks.addAll(this.getBlocks());
		newBlocks.addAll(other.getBlocks());
		return new ApplicationInformation(id, name, newBlocks);
	}

	public ApplicationID getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns the UUIDs of all Modules that run parts of this Application.
	 * 
	 * @return the UUIDs of all Modules that run parts of this Application
	 */
	public Collection<UUID> getModules() {
		return blocksByModules.keySet();
	}

	/**
	 * Returns a Map from ModuleID to all FunctionBlocks running on that particular Module.
	 * 
	 * @return a Map from ModuleID to FunctionBlocks running on the Module
	 */
	public Map<UUID, Collection<BlockInformation>> getBlocksByModules() {
		return blocksByModules;
	}

	/**
	 * Returns all FunctionBlocks that belong to this Application.
	 * 
	 * @return all FunctionBlocks belonging to this Application
	 */
	public Collection<BlockInformation> getBlocks() {
		final Collection<BlockInformation> blocks = new ArrayList<BlockInformation>();
		for (final Collection<BlockInformation> blocksOnModule : blocksByModules.values()) {
			blocks.addAll(blocksOnModule);
		}
		return blocks;
	}

	/**
	 * Returns all FunctionBlocks belonging to this Application that run on the given Module.
	 * 
	 * @param moduleID
	 *            all FunctionBlocks belonging to this Application running on this Module will be returned
	 * @return all FunctionBlocks belonging to this Application that run on the given Module
	 */
	public Collection<BlockInformation> getBlocks(final UUID moduleID) {
		final Collection<BlockInformation> blocks = blocksByModules.get(moduleID);
		if (blocks == null) {
			return Collections.emptyList();
		}
		return blocks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((blocksByModules == null) ? 0 : blocksByModules.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApplicationInformation other = (ApplicationInformation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (blocksByModules == null) {
			if (other.blocksByModules != null)
				return false;
		} else if (!blocksByModules.equals(other.blocksByModules))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ApplicationInformation[id=" + id + ",name=" + name + ",blocksByModules=" + blocksByModules + "]";
	}
}
