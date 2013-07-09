package edu.teco.dnd.tests;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.util.NetConnection;

public class TestConfigReader extends ConfigReader {

	private String name;
	private UUID uuid = UUID.randomUUID();
	private int maxAppthreads = 0;
	private boolean allowNIO = true;
	private InetSocketAddress[] listen;
	private InetSocketAddress[] announce;
	private NetConnection[] multicast;
	private BlockTypeHolder allowedBlocks; // the rootBlock

	private static transient final Logger LOGGER = LogManager.getLogger(TestConfigReader.class);

	private transient Map<String, BlockTypeHolder> blockQuickaccess = new HashMap<String, BlockTypeHolder>();

	public TestConfigReader(String name, UUID uuid, int maxAppthreads,boolean allowNIO, InetSocketAddress[] listen,
			InetSocketAddress[] announce, NetConnection[] multicast, BlockTypeHolder allowedBlocks) {

		this.name = name;
		this.uuid = uuid;
		this.maxAppthreads = maxAppthreads;
		this.allowNIO = allowNIO;
		this.listen = listen;
		this.announce = announce;
		this.multicast = multicast;
		this.allowedBlocks = allowedBlocks;

		if (allowedBlocks != null) {
			fillTransientVariables(blockQuickaccess, allowedBlocks);
		}
	}

	private void fillTransientVariables(Map<String, BlockTypeHolder> blockQuickaccess,
			final BlockTypeHolder currentBlock) {
		Set<BlockTypeHolder> children = currentBlock.getChildren();
		if (children == null) {
			blockQuickaccess.put(currentBlock.type, currentBlock);
		} else {
			for (BlockTypeHolder child : currentBlock.getChildren()) {
				child.setParent(currentBlock);
				fillTransientVariables(blockQuickaccess, child);
			}
		}

	}

	@Override
	public void load(String path) throws IOException {
		throw LOGGER.throwing(new NotImplementedException());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public int getMaxThreadsPerApp() {
		return (maxAppthreads > 0) ? maxAppthreads : ConfigReader.DEFAULT_THREADS_PER_APP;
	}

	@Override
	public InetSocketAddress[] getListen() {
		return listen;
	}

	@Override
	public InetSocketAddress[] getAnnounce() {
		return announce;
	}

	@Override
	public NetConnection[] getMulticast() {
		return multicast;
	}

	@Override
	public BlockTypeHolder getBlockRoot() {
		return allowedBlocks;
	}

	@Override
	public Map<String, BlockTypeHolder> getAllowedBlocks() {
		return blockQuickaccess;
	}

	@Override
	public boolean getAllowNIO() {
		return allowNIO;
	}

}
