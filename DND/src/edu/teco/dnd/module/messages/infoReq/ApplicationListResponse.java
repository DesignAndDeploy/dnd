package edu.teco.dnd.module.messages.infoReq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

/**
 * Contains the UUIDs and names of all running Applications.
 * 
 */
public class ApplicationListResponse extends Response {

	public static final String MESSAGE_TYPE = "application list";

	/**
	 * The UUIDs of all running applications mapped to their names.
	 */
	private final Map<UUID, String> applicationNames;
	/**
	 * The UUIDs of all running applications mapped to the UUID of the blocks.
	 */
	private final Map<UUID, Collection<UUID>> applicationBlocks;
	/**
	 * Maps the UUID of a function block to its type.
	 */
	private final Map<UUID, String> uuidToBlockType;

	/**
	 * Maps the BlockID of a function block to its name.
	 */
	private final Map<BlockID, String> blockIDToBlockName;

	/**
	 * The UUID of the module this Response was generated on.
	 */
	private final UUID moduleUUID;

	/**
	 * Creates a new ApplicationListResponse.
	 * 
	 * @param applicationNames
	 *            the UUIDs of the running Applications mapped to their names
	 * @param moduleUUID
	 *            the UUID of the module sending this.
	 * @param applicationBlocks
	 *            the UUIDs of the running applications mapped to the Blocks they execute.
	 * @param blockIDToBlockType
	 *            A Map from BlockIDs of blocks running on the specified module to their type.
	 */
	public ApplicationListResponse(final UUID moduleUUID, final Map<UUID, String> applicationNames,
			Map<UUID, Collection<UUID>> applicationBlocks, final Map<UUID, String> uuidToBlockType,
			final Map<BlockID, String> blockIDToBlockName) {
		this.moduleUUID = moduleUUID;
		this.applicationNames = Collections.unmodifiableMap(new HashMap<UUID, String>(applicationNames));
		this.uuidToBlockType = Collections.unmodifiableMap(new HashMap<UUID, String>(uuidToBlockType));
		this.blockIDToBlockName = Collections.unmodifiableMap(new HashMap<BlockID, String>(blockIDToBlockName));
		Map<UUID, Collection<UUID>> blocks = new HashMap<UUID, Collection<UUID>>();
		for (final Entry<UUID, Collection<UUID>> entry : applicationBlocks.entrySet()) {
			blocks.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<UUID>(entry.getValue())));
		}
		this.applicationBlocks = Collections.unmodifiableMap(blocks);
	}

	/**
	 * Constructor used by gson.
	 */
	// FIXME: add adapter that keeps the maps unmodifiable.
	// Why? This will only work AFTER sending, from which point on we do not care. Nothing left that can break on our
	// part and nobody is going to modify a map by mistake anyway. MM
	@SuppressWarnings("unused")
	private ApplicationListResponse() {
		this.moduleUUID = null;
		this.applicationNames = null;
		this.applicationBlocks = null;
		this.uuidToBlockType = null;
		this.blockIDToBlockName = null;
	}

	/**
	 * Returns the UUID of the module this Response was generated on.
	 * 
	 * @return the UUID of the module this Response was generated on
	 */
	public UUID getModuleUUID() {
		return this.moduleUUID;
	}

	/**
	 * Returns the UUIDs of all running Applications mapped to their name.
	 * 
	 * @return the UUIDs of all running Applications mapped to their name
	 */
	public Map<UUID, String> getApplicationNames() {
		return this.applicationNames;
	}

	/**
	 * Returns a Map from all UUIDs of function blocks running on this module to their Types.
	 * 
	 * @return Map from block UUID to their type.
	 */
	public Map<UUID, String> getBlockTypes() {
		return this.uuidToBlockType;
	}
	
	/**
	 * Returns a Map from all BlockIDs of function blocks running on this module to their names.
	 * 
	 * @return Map from block BlockID to the name of the block.
	 */
	public Map<BlockID, String> getBlockNames(){
		return this.blockIDToBlockName;
	}

	/**
	 * Returns the UUIDs of all running Applications mapped to their running Map<BlockUUID,FunctionBlock>.
	 * 
	 * @return the UUIDs of all running Applications mapped to their running Map<BlockUUID,FunctionBlock>
	 */
	public Map<UUID, Collection<UUID>> getApplicationBlocks() {
		return this.applicationBlocks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((applicationNames == null) ? 0 : applicationNames.hashCode());
		result = prime * result + ((uuidToBlockType == null) ? 0 : uuidToBlockType.hashCode());
		result = prime * result + ((moduleUUID == null) ? 0 : moduleUUID.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ApplicationListResponse other = (ApplicationListResponse) obj;
		if (applicationNames == null) {
			if (other.applicationNames != null) {
				return false;
			}
		} else if (!applicationNames.equals(other.applicationNames)) {
			return false;
		}
		if (uuidToBlockType == null) {
			if (other.applicationNames != null) {
				return false;
			}
		} else if (!uuidToBlockType.equals(other.uuidToBlockType)) {
			return false;
		}
		if (moduleUUID == null) {
			if (other.moduleUUID != null) {
				return false;
			}
		} else if (!moduleUUID.equals(other.moduleUUID)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ApplicationListResponse [applications=" + applicationNames + ", moduleUUID=" + moduleUUID
				+ ", getSourceUUID()=" + getSourceUUID() + "]";
	}
}
