package edu.teco.dnd.module.config.tests;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.config.ModuleConfig;
import edu.teco.dnd.util.NetConnection;

/**
 * Mock ModuleConfig used to simulate a real configuration for testing purposes.
 * 
 * @author Marvin Marx
 * 
 */
public class TestConfigReader implements ModuleConfig {

	private String name;
	private String location;
	private ModuleID moduleID = new ModuleID();
	private int maxAppthreads = 0;
	private Collection<InetSocketAddress> listen;
	private Collection<InetSocketAddress> announce;
	private Collection<NetConnection> multicast;
	private BlockTypeHolder allowedBlocks; // the rootBlock
	private int currentBlockId = 0;

	/**
	 * set up this mock according to parameters.
	 * 
	 * @param name
	 *            moduleName (humanReadable)
	 * @param location
	 *            module location
	 * @param moduleID
	 *            moduleUUID...
	 * @param maxAppthreads
	 *            maximum allowed threads per application.
	 * @param allowNIO
	 *            whether to allow using JavaNIO
	 * @param listen
	 *            addresses to listen on for incoming other modules.
	 * @param announce
	 *            addresses to announce ones own presence on
	 * @param multicast
	 *            address to multicast announce ones own presence on
	 * @param allowedBlocks
	 *            Tree of blockTypes allowed to run (see BlockTypeHolder)
	 */
	public TestConfigReader(String name, String location, ModuleID moduleID, int maxAppthreads, boolean allowNIO,
			Collection<InetSocketAddress> listen, Collection<InetSocketAddress> announce,
			Collection<NetConnection> multicast, BlockTypeHolder allowedBlocks) {

		this.name = name;
		this.location = location;
		this.moduleID = moduleID;
		this.maxAppthreads = maxAppthreads;
		this.listen = listen;
		this.announce = announce;
		this.multicast = multicast;
		this.allowedBlocks = allowedBlocks;

		if (allowedBlocks != null) {
			fillTransientVariables(allowedBlocks);
		}
	}

	/**
	 * Convenience method to get a TestConfigReader, with some arbitrarily chosen values in it.
	 * 
	 * @return the TestConfigReader
	 * @throws SocketException
	 *             if the same is thrown while trying to get the multicast address.
	 */
	public static TestConfigReader getPredefinedReader() throws SocketException {
		String name = "ConfReadName";
		String location = "location";
		ModuleID moduleID = new ModuleID(UUID.fromString("12345678-9abc-def0-1234-56789abcdef0"));
		int maxAppthreads = 0;

		Collection<InetSocketAddress> listen = new ArrayList<InetSocketAddress>();
		listen.add(new InetSocketAddress("localhost", 8888));
		listen.add(new InetSocketAddress("127.0.0.1", 4242));
		Collection<InetSocketAddress> announce = new ArrayList<InetSocketAddress>();
		announce.add(new InetSocketAddress("localhost", 8888));
		Collection<NetConnection> multicast = new ArrayList<NetConnection>();
		multicast.add(new NetConnection(new InetSocketAddress("255.0.0.1", 1212), NetworkInterface.getNetworkInterfaces()
						.nextElement()));

		Set<BlockTypeHolder> secondLevelChild = new HashSet<BlockTypeHolder>();
		secondLevelChild.add(new BlockTypeHolder("child1TYPE", 2));
		secondLevelChild.add(new BlockTypeHolder("child2TYPE", 2));

		Set<BlockTypeHolder> firstLevelChild = new HashSet<BlockTypeHolder>();
		firstLevelChild.add(new BlockTypeHolder("child2TYPE", 1));

		firstLevelChild.add(new BlockTypeHolder(secondLevelChild, 1));
		BlockTypeHolder allowedBlocks = new BlockTypeHolder(firstLevelChild, 0);

		return new TestConfigReader(name, location, moduleID, maxAppthreads, true, listen, announce, multicast,
				allowedBlocks);
	}

	/**
	 * fill the internal transient variables of blockTypeHolder which are usually not filled in during storage (and its
	 * more convenient than to paste it every time we need a mock).
	 * 
	 * @param currentBlock
	 *            the root of the BlockTypeHolder tree if not called recursively.
	 */
	private void fillTransientVariables(final BlockTypeHolder currentBlock) {
		currentBlock.setAmountLeft(currentBlock.getAmountAllowed());
		currentBlock.setIdNumber(++currentBlockId);
		Set<BlockTypeHolder> children = currentBlock.getChildren();
		if (children != null) {
			for (BlockTypeHolder child : currentBlock.getChildren()) {
				child.setParent(currentBlock);
				fillTransientVariables(child);
			}
		}

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
		return moduleID;
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
		return 2;
	}

}
