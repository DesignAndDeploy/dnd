package edu.teco.dnd.module;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Configuration wrapper providing convenient accessors to Module configurations.
 */
public class ModuleConfig implements Serializable {
	/**
	 * Property name of the name attribute.
	 */
	public static final String NAME_ID = "name";

	/**
	 * Property name of the location attribute.
	 */
	public static final String LOCATION_ID = "location";

	/**
	 * Property name of the blockNumber attribute.
	 */
	public static final String BLOCK_NUMBER_ID = "blockNumber";

	/**
	 * Property name of the memory attribute.
	 */
	public static final String MEMORY_ID = "memory";

	/**
	 * Property name of the mhz attribute.
	 */
	public static final String MHZ_ID = "cpuMHz";

	/**
	 * Property name of the supportedBlockIds attribute.
	 */
	public static final String BLOCKS_ID = "blocks";

	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 8894216946419028486L;

	/**
	 * ConfigFile used for initialization and saving.
	 */
	private ConfigFile config;

	/**
	 * The name of the block.
	 */
	private String name;

	/**
	 * The location of the block.
	 */
	private String location;

	/**
	 * The maximum number of blocks that can be run on the module.
	 */
	private int maxNumberOfBlocks;

	/**
	 * The amount of memory.
	 */
	private int memory;

	/**
	 * The speed of the CPU in MHz.
	 */
	private int mhz;

	/**
	 * A list of supported block ids.
	 */
	private List<String> supportedBlockIds = new ArrayList<>();

	/**
	 * Initializes a ModuleConfig by reading the given config. This will also be set as the ConfigFile to use.
	 * 
	 * @param config
	 *            the config file to read. Must not be null.
	 */
	public ModuleConfig(final ConfigFile config) {
		setAndReadConfig(config);
	}

	/**
	 * Initializes a ModuleConfig with default settings.
	 */
	public ModuleConfig() {
		setDefaults();
	}

	/**
	 * Sets the default values.
	 */
	public final void setDefaults() {
		name = "";
		location = "";
		maxNumberOfBlocks = 0;
		memory = 0;
		mhz = 0;
		supportedBlockIds.clear();
	}

	/**
	 * Sets the config file to use and reads the values provided in the file.
	 * 
	 * @param config
	 *            the config file to use. Must not be null.
	 */
	public final void setAndReadConfig(final ConfigFile config) {
		if (config == null) {
			throw new IllegalArgumentException("config must not be null");
		}

		setConfig(config);

		setName(config.getProperty(NAME_ID));
		setLocation(config.getProperty(LOCATION_ID));
		setMaxNumberOfBlocks(tryInt(config.getProperty(BLOCK_NUMBER_ID)));
		setMemory(tryInt(config.getProperty(MEMORY_ID)));
		setCpuMHz(tryInt(config.getProperty(MHZ_ID)));

		supportedBlockIds.clear();
		for (String id : splitString(config.getProperty(BLOCKS_ID))) {
			supportedBlockIds.add(id);
		}
	}

	/**
	 * Sets the config file use for writing. Current values are kept.
	 * 
	 * @param config
	 *            the config file to use.
	 */
	public final void setConfig(final ConfigFile config) {
		this.config = config;
	}

	/**
	 * Tries to convert s into a number. If not possible returns 0.
	 * 
	 * @param s
	 *            the String that should be converted.
	 * @return the value of s or 0 if a conversion was not possible.
	 */
	private static int tryInt(final String s) {
		int value = 0;
		try {
			value = Integer.valueOf(s);
		} catch (NumberFormatException e) {
		}
		return value;
	}

	/**
	 * Converts a comma separated String into a list of Strings. If null or an empty String is passed, an
	 * empty array is returned.
	 * 
	 * @param s
	 *            the String to read
	 * @return an array containing all Strings in the comma separated list
	 */
	private static String[] splitString(final String s) {
		if (s == null || s.isEmpty()) {
			return new String[0];
		}
		String[] ret = s.split(",");
		for (int i = 0; i < ret.length; i++) {
			ret[i] = ret[i].trim();
		}
		return ret;
	}

	/**
	 * Returns the name of the module. Will never return null.
	 * 
	 * @return the name of this module
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name of the module. null will be replaced with an empty string.
	 * 
	 * @param name
	 *            the name to set
	 */
	public final void setName(final String name) {
		if (name == null) {
			this.name = "";
		} else {
			this.name = name;
		}
	}

	/**
	 * Returns the location of the module. Will never return null;
	 * 
	 * @return the location of this module
	 */
	public final String getLocation() {
		return location;
	}

	/**
	 * Sets the location of the module. null will be replaced with an empty string.
	 * 
	 * @param location
	 *            the location to set
	 */
	public final void setLocation(final String location) {
		if (location == null) {
			this.location = "";
		} else {
			this.location = location;
		}
	}

	/**
	 * Returns the maximum number of blocks. Will always be zero or bigger.
	 * 
	 * @return the maximum number of blocks
	 */
	public final int getMaxNumberOfBlocks() {
		return maxNumberOfBlocks;
	}

	/**
	 * Sets the maximum number of blocks. Negative values will be normalized to 0.
	 * 
	 * @param maxNumberOfBlocks
	 *            the maximum number of blocks
	 */
	public final void setMaxNumberOfBlocks(final int maxNumberOfBlocks) {
		if (maxNumberOfBlocks <= 0) {
			this.maxNumberOfBlocks = 0;
		} else {
			this.maxNumberOfBlocks = maxNumberOfBlocks;
		}
	}

	/**
	 * Returns the amount of memory. Will always be zero or bigger.
	 * 
	 * @return the memory of this module
	 */
	public final int getMemory() {
		return memory;
	}

	/**
	 * Sets the amount of memory. Negative numbers will be normalized to zero.
	 * 
	 * @param memory
	 *            the memory to set
	 */
	public final void setMemory(final int memory) {
		if (memory <= 0) {
			this.memory = 0;
		} else {
			this.memory = memory;
		}
	}

	/**
	 * Returns the speed of the CPU in MHz. Will always be zero or bigger.
	 * 
	 * @return the CPZ speed in MHz
	 */
	public final int getCpuMHz() {
		return mhz;
	}

	/**
	 * Sets the speed of the CPU in MHz. Negative numbers will be normalized to zero.
	 * 
	 * @param mhz
	 *            the speed of the CPU
	 */
	public final void setCpuMHz(final int mhz) {
		this.mhz = mhz;
	}

	/**
	 * Returns a List containing the supported block ids.
	 * 
	 * @return a List containing the supported block ids
	 */
	public final List<String> getSupportedBlockId() {
		return supportedBlockIds;
	}

	/**
	 * Adds an id to the list of supported ids.
	 * 
	 * @param id
	 *            the ID to add to the list of supported IDs. Must not be null.
	 */
	public final void addSupportedBlockId(final String id) {
		if (id == null) {
			throw new IllegalArgumentException("id must not be null");
		}
		supportedBlockIds.add(id);
	}

	/**
	 * Removes an id from the list of supported ids.
	 * 
	 * @param id
	 *            the ID to remove from the list of supported IDs.
	 */
	public final void removeSupportedBlockId(final String id) {
		supportedBlockIds.remove(id);
	}

	/**
	 * Joins the objects of a collection into a String separated by a given separator.
	 * 
	 * @param collection
	 *            the collection to join. Must not be null.
	 * @param separator
	 *            The String to add between the objects
	 * @return a String formed by concatenating the objects in the collection and adding the separator in
	 *         between
	 */
	private static String join(final Collection<?> collection, final String separator) {
		if (collection == null) {
			throw new IllegalArgumentException("collection must not be null");
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		String sep;

		if (separator == null) {
			sep = "";
		} else {
			sep = separator;
		}

		for (Object obj : collection) {
			if (first) {
				first = false;
			} else {
				sb.append(sep);
			}
			sb.append(obj);
		}

		return sb.toString();
	}

	/**
	 * Saves the changes made to the properties to the file on the disk. If no config file has been set,
	 * nothing is done.
	 * 
	 * @throws IOException
	 *             if an error occurs during saving
	 */
	public final void save() throws IOException {
		if (config != null) {
			config.setProperty(NAME_ID, name);
			config.setProperty(LOCATION_ID, location);
			config.setProperty(BLOCK_NUMBER_ID, Integer.toString(maxNumberOfBlocks));
			config.setProperty(MEMORY_ID, Integer.toString(memory));
			config.setProperty(MHZ_ID, Integer.toString(mhz));
			config.setProperty(BLOCKS_ID, join(supportedBlockIds, ","));
			config.save();
		}
	}
}
