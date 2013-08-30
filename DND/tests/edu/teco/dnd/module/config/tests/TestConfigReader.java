package edu.teco.dnd.module.config.tests;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.util.NetConnection;

/**
 * Mock ConfigReader used to simulate a real configuration for testing purposes.
 * 
 * @author Marvin Marx
 * 
 */
public class TestConfigReader extends ConfigReader {

	private String name;
	private UUID moduleUuid = UUID.randomUUID();
	private int maxAppthreads = 0;
	private boolean allowNIO = true;
	private InetSocketAddress[] listen;
	private InetSocketAddress[] announce;
	private NetConnection[] multicast;
	private BlockTypeHolder allowedBlocks; // the rootBlock
	private int currentBlockId = 0;

	private static final transient Logger LOGGER = LogManager.getLogger(TestConfigReader.class);

	private transient Map<String, BlockTypeHolder> blockQuickaccess = new HashMap<String, BlockTypeHolder>();
	private transient Map<Integer, BlockTypeHolder> blockIdQuickaccess = new HashMap<Integer, BlockTypeHolder>();

	/**
	 * set up this mock according to parameters.
	 * 
	 * @param name
	 *            moduleName (humanReadable)
	 * @param moduleUuid
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
	public TestConfigReader(String name, UUID moduleUuid, int maxAppthreads, boolean allowNIO,
			InetSocketAddress[] listen, InetSocketAddress[] announce, NetConnection[] multicast,
			BlockTypeHolder allowedBlocks) {

		this.name = name;
		this.moduleUuid = moduleUuid;
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

	/**
	 * Convenience method to get a TestConfigReader, with some arbitrarily chosen values in it.
	 * 
	 * @return the TestConfigReader
	 * @throws SocketException
	 *             if the same is thrown while trying to get the multicast address.
	 */
	public static TestConfigReader getPredefinedReader() throws SocketException {
		String name = "ConfReadName";
		UUID moduleUuid = UUID.fromString("12345678-9abc-def0-1234-56789abcdef0");
		int maxAppthreads = 0;

		InetSocketAddress[] listen = new InetSocketAddress[2];
		listen[0] = new InetSocketAddress("localhost", 8888);
		listen[1] = new InetSocketAddress("127.0.0.1", 4242);
		InetSocketAddress[] announce = new InetSocketAddress[1];
		announce[0] = new InetSocketAddress("localhost", 8888);
		NetConnection[] multicast = new NetConnection[1];
		multicast[0] =
				new NetConnection(new InetSocketAddress("255.0.0.1", 1212), NetworkInterface.getNetworkInterfaces()
						.nextElement());

		Set<BlockTypeHolder> secondLevelChild = new HashSet<BlockTypeHolder>();
		secondLevelChild.add(new BlockTypeHolder("child1TYPE", 2));
		secondLevelChild.add(new BlockTypeHolder("child2TYPE", 2));

		Set<BlockTypeHolder> firstLevelChild = new HashSet<BlockTypeHolder>();
		firstLevelChild.add(new BlockTypeHolder("child2TYPE", 1));

		firstLevelChild.add(new BlockTypeHolder(secondLevelChild, 1));
		BlockTypeHolder allowedBlocks = new BlockTypeHolder(firstLevelChild, 0);

		return new TestConfigReader(name, moduleUuid, maxAppthreads, true, listen, announce, multicast, allowedBlocks);
	}

	/**
	 * fill the internal transient variables of blockTypeHolder which are usually not filled in during storage (and its
	 * more convenient than to paste it every time we need a mock).
	 * 
	 * @param currentBlockQuickaccess
	 *            blockQuickaccess list. Used for easier retrieval of types.
	 * @param currentBlock
	 *            the root of the BlockTypeHolder tree if not called recursively.
	 */
	private void fillTransientVariables(Map<String, BlockTypeHolder> currentBlockQuickaccess,
			final BlockTypeHolder currentBlock) {
		currentBlock.setAmountLeft(currentBlock.getAmountAllowed());
		currentBlock.setIdNumber(++currentBlockId);
		blockIdQuickaccess.put(currentBlock.getIdNumber(), currentBlock);
		Set<BlockTypeHolder> children = currentBlock.getChildren();
		if (children == null) {
			currentBlockQuickaccess.put(currentBlock.getType(), currentBlock);
		} else {
			for (BlockTypeHolder child : currentBlock.getChildren()) {
				child.setParent(currentBlock);
				fillTransientVariables(currentBlockQuickaccess, child);
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
		return moduleUuid;
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
	public boolean getAllowNIO() {
		return allowNIO;
	}

	@Override
	public Map<Integer, BlockTypeHolder> getAllowedBlocksById() {
		return blockIdQuickaccess;
	}

	@Override
	public int getAnnounceInterval() {
		return 2;
	}

}
