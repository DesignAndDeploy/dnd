package edu.teco.dnd.module.config;

import java.net.NetworkInterface;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.eclipse.EclipseUtil;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class ConfigReader {
	
	private static final Logger LOGGER = LogManager.getLogger(EclipseUtil.class);
	
	/**
	 * restores the config from a savefile
	 * 
	 * @param path
	 *            the savefile is stored in (can be e.g. a url if class is
	 *            prepared to handle this, however pathes with special meaning
	 *            must not map to real FS pathes)
	 * @return false only if action failed. True if unsure/successfull)
	 */
	public abstract boolean restore(String path);

	/** optional, override if desired */
	public boolean save(String path) {
		LOGGER.warn("saving not implemented.");
		return false;
	}
	public abstract String getName();

	public abstract UUID getUuid();

	public abstract NetworkInterface[] getListen();

	public abstract NetworkInterface[] getAnnounce();

	public abstract NetworkInterface[] getMulticast();

	/**
	 * @return a set of blocks allowed to run and their amounts (encoded in
	 *         BlockType). Key is the <i>name</i> of the block/(group of blocks)
	 *         (by definition).
	 */
	public abstract Set<BlockType> getAllowedBlocks();

	/**
	 * only name is compared in equals!
	 * 
	 * @author cryptkiddy
	 * 
	 */
	protected static class BlockType {
		String name;
		/** null if none */
		BlockType supertype = null;
		/** allowed blocks of this type, <0 means infinity. */
		int amount = -1;

		@Override
		public int hashCode() {
			final int prime = 31;
			return prime + ((name == null) ? 0 : name.hashCode());
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			BlockType other = (BlockType) obj;

			if ((name == null && other.name != null)
					|| !name.equals(other.name)) {
				return false;
			}
			return true;
		}

	}

}
