package edu.teco.dnd.module.config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.util.NetConnection;

/**
 * abstract class providing hooks for reading a configuration file. Initialized with a string which might be a Reference
 * to a path, or anything else according to concrete implementation.
 * 
 * @author Marvin Marx
 * 
 */
public abstract class ConfigReader {

	private static final Logger LOGGER = LogManager.getLogger(ConfigReader.class);
	/**
	 * default amount of threads each application on a module is allowed to execute if not given in the configuration
	 * file.
	 */
	public static final int DEFAULT_THREADS_PER_APP = 3;

	/**
	 * restores the config from a savefile.
	 * 
	 * @param path
	 *            path the savefile is stored in (can be e.g. a url if concrete childclass is prepared to handle this.)
	 * @throws IOException
	 *             if reading the file failed.
	 */
	public abstract void load(String path) throws IOException;

	/**
	 * optional, override if desired safe the current configuration.
	 * 
	 * @return false if not implemented or saving failed.
	 */
	public boolean store() {
		LOGGER.warn("saving not implemented.");
		return false;
	}

	/**
	 * @return Name of the module.
	 */
	public abstract String getName();

	/**
	 * Returns the location of the Module. May be null.
	 * 
	 * @return the location of the Module or null if unset.
	 */
	public abstract String getLocation();

	/**
	 * @return the uuid of this module.
	 */
	public abstract UUID getUuid();

	/**
	 * @return the maximum number of allowed processes per application
	 */
	public abstract int getMaxThreadsPerApp();

	/**
	 * @return the maximum number of allowed processes per application
	 */
	public abstract boolean getAllowNIO();

	/**
	 * @return thethe socket to listen on for incoming connections
	 */
	public abstract InetSocketAddress[] getListen();

	/**
	 * @return the addresses to try to connect to.
	 */
	public abstract InetSocketAddress[] getAnnounce();

	/**
	 * @return the multicast address, to announce own presence to the network.
	 */
	public abstract NetConnection[] getMulticast();

	/**
	 * @return a tree structure of blocks and their allowed amount (encoded in blocktype). This returns the root of the
	 *         tree. See getAllowedBlocks() for a Map version.
	 */
	public abstract BlockTypeHolder getBlockRoot();

	/**
	 * @return a set of blocks allowed to run and their amounts (encoded in BlockType). Key is the <i>internal id
	 *         number</i> of the block.
	 */
	public abstract Map<Integer, BlockTypeHolder> getAllowedBlocksById();

	/**
	 * Returns the number of seconds that should be waited between sending two beacons.
	 * 
	 * @return the number of seconds to wait between to beacons
	 */
	public abstract int getAnnounceInterval();

}
