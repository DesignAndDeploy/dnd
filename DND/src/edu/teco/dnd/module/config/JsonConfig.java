package edu.teco.dnd.module.config;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.util.NetConnection;

public class JsonConfig implements ModuleConfig {
	private static final transient Logger LOGGER = LogManager.getLogger(JsonConfig.class);

	private static final int DEFAULT_MAX_APP_THREADS = 3;
	private static final int DEFAULT_ANNOUNCE_INTERVAL = 5;

	// values are initialized with their defaults; If they are missing from the configuration file these values are used
	private String name;
	private String location;
	private UUID uuid;
	private int maxAppthreads;
	private int announceInterval;
	private Collection<InetSocketAddress> listen;
	private Collection<InetSocketAddress> announce;
	private Collection<NetConnection> multicast;
	private BlockTypeHolder allowedBlocks;

	private JsonConfig() {
	}

	void initialize() {
		LOGGER.entry();
		normalize();
		initializeAllowedBlocks();
		makeCollectionsUnmodifiable();
		LOGGER.exit();
	}

	private void normalize() {
		name = name == null ? "" : name;
		location = location == null ? "" : location;
		uuid = uuid == null ? UUID.randomUUID() : uuid;
		maxAppthreads = maxAppthreads <= 0 ? DEFAULT_MAX_APP_THREADS : maxAppthreads;
		announceInterval = announceInterval <= 0 ? DEFAULT_ANNOUNCE_INTERVAL : announceInterval;
		listen = listen == null ? Collections.<InetSocketAddress> emptyList() : listen;
		announce = announce == null ? Collections.<InetSocketAddress> emptyList() : announce;
		multicast = multicast == null ? Collections.<NetConnection> emptyList() : multicast;
	}

	private void initializeAllowedBlocks() {
		LOGGER.entry();
		if (allowedBlocks == null) {
			LOGGER.exit();
			return;
		}

		int currentBlockId = 0;
		final Queue<BlockTypeHolder> queue = new LinkedList<BlockTypeHolder>();
		LOGGER.trace("adding {} to queue", allowedBlocks);
		queue.add(allowedBlocks);

		while (!queue.isEmpty()) {
			final BlockTypeHolder currentBlock = queue.remove();
			LOGGER.trace("initializing {}", currentBlock);

			currentBlock.setAmountLeft(currentBlock.getAmountAllowed());
			currentBlock.setID(++currentBlockId);

			for (BlockTypeHolder child : currentBlock.getChildren()) {
				if (child != null) {
					child.setParent(currentBlock);
					LOGGER.trace("adding {} to queue", child);
					queue.add(child);
				}
			}
		}
		LOGGER.exit();
	}

	private void makeCollectionsUnmodifiable() {
		listen = Collections.unmodifiableCollection(listen);
		announce = Collections.unmodifiableCollection(announce);
		multicast = Collections.unmodifiableCollection(multicast);
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
