package edu.teco.dnd.module.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.util.InetSocketAddressAdapter;
import edu.teco.dnd.util.NetConnection;
import edu.teco.dnd.util.NetConnectionAdapter;

/**
 * Concrete implementation of a configuration Reader. Initialized with a Path to a jsonConfiguration file.
 * 
 * @author Marvin Marx
 * 
 */
public class JsonConfig implements ModuleConfig {
	private static final transient Logger LOGGER = LogManager.getLogger(JsonConfig.class);
	private static final transient Gson GSON;
	private static final int DEFAULT_ANNOUNCE_INTERVAL = 5;

	private String name;
	private String location;
	private UUID uuid = UUID.randomUUID();
	private int maxAppthreads = 0;
	private int announceInterval = DEFAULT_ANNOUNCE_INTERVAL;
	private Collection<InetSocketAddress> listen;
	private Collection<InetSocketAddress> announce;
	private Collection<NetConnection> multicast;
	private BlockTypeHolder allowedBlocks; // the rootBlock
	private transient int currentBlockId = 0;
	private String pathToSaveTo = null;

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(InetSocketAddress.class, new InetSocketAddressAdapter());
		builder.registerTypeAdapter(NetConnection.class, new NetConnectionAdapter());
		final ExclusionStrategy amountLeftExclusionStrategy = new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(final FieldAttributes f) {
				return BlockTypeHolder.class.equals(f.getDeclaringClass()) && "amountLeft".equals(f.getName());
			}

			@Override
			public boolean shouldSkipClass(final Class<?> clazz) {
				return false;
			}
		};
		builder.addDeserializationExclusionStrategy(amountLeftExclusionStrategy);
		builder.addSerializationExclusionStrategy(amountLeftExclusionStrategy);
		GSON = builder.create();
	}

	/**
	 * create an empty Config to set parameters manually.
	 */
	public JsonConfig() {
	}

	/**
	 * creates a new JsonConfig, by loading the given file from path and parsing it as Json.
	 * 
	 * @param path
	 *            the path to load from.
	 * @throws IOException
	 *             of an error with the file exists.
	 */
	public JsonConfig(String path) throws IOException {
		this.load(path);
	}

	/**
	 * Set this configuration to have all the same variables as an old configuration given as parameter.
	 * 
	 * @param oldConf
	 *            the config to set to.
	 */
	public void setTo(JsonConfig oldConf) {
		if (oldConf == null) {
			LOGGER.warn("Invalid Config to set(config was null)");
			throw new IllegalArgumentException("oldConf must not be null");
		}

		this.name = oldConf.name;
		this.location = oldConf.location;
		this.maxAppthreads = oldConf.maxAppthreads;
		if (oldConf.uuid != null) {
			this.uuid = oldConf.uuid;
		}
		this.announceInterval = oldConf.announceInterval;
		this.listen = oldConf.listen;
		this.announce = oldConf.announce;
		this.multicast = oldConf.multicast;
		this.allowedBlocks = oldConf.allowedBlocks;
	}

	public void load(String path) throws IOException {
		pathToSaveTo = path;
		FileReader reader = null;
		try {
			reader = new FileReader(path);
			setTo(GSON.fromJson(reader, this.getClass()));
		} catch (FileNotFoundException e) {
			LOGGER.catching(Level.WARN, e);
			throw e;
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				LOGGER.catching(Level.INFO, e);
			}
		}

		if (allowedBlocks != null) {
			fillTransientVariables(allowedBlocks);
		}
	}

	/**
	 * After loading the blocks from a stored Json configuration, some parameters remain unset for practical reasons.
	 * This method fills the parameters recursively.
	 * 
	 * @param currentBlock
	 *            the block currently being worked on. On first (non recursing) invocation this is naturally the root of
	 *            the tree.
	 */
	private void fillTransientVariables(final BlockTypeHolder currentBlock) {
		currentBlock.setAmountLeft(currentBlock.getAmountAllowed());
		currentBlock.setIdNumber(++currentBlockId);
		if (!currentBlock.isLeaf()) {
			for (BlockTypeHolder child : currentBlock.getChildren()) {
				child.setParent(currentBlock);
				fillTransientVariables(child);
			}
		}
	}

	public boolean store() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(pathToSaveTo);
			GSON.toJson(this, writer);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				// ignoring.
			}
		}
		return true;
	}

	/**
	 * Set the path that the configuration will be safed to upon calling safe().
	 * 
	 * @param path
	 *            the path
	 */
	public void setPathToSaveTo(String path) {
		pathToSaveTo = path;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public ModuleID getModuleID() {
		return new ModuleID(uuid);
	}

	@Override
	public int getMaxThreadsPerApp() {
		return maxAppthreads;
	}

	@Override
	public Collection<InetSocketAddress> getListen() {
		return listen;
	}

	@Override
	public Collection<InetSocketAddress> getAnnounce() {
		return announce;
	}

	@Override
	public Collection<NetConnection> getMulticast() {
		return multicast;
	}

	@Override
	public BlockTypeHolder getBlockRoot() {
		return allowedBlocks;
	}

	@Override
	public int getAnnounceInterval() {
		return announceInterval;
	}
}
