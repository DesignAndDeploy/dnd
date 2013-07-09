package edu.teco.dnd.tests;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.module.ModuleMain;
import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.network.TCPConnectionManager;
import edu.teco.dnd.util.NetConnection;

public class ModuleMainTest {

	public static void main(final String[] args) throws SocketException {

		Set<ConfigReader> configs = getTestConfigs();

		for (final ConfigReader reader : configs) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("starting modmain");

					TCPConnectionManager connectionManager = ModuleMain.prepareNetwork(reader);
					ModuleApplicationManager appMan = new ModuleApplicationManager(reader, connectionManager);
					ModuleMain.registerHandler(reader, connectionManager, appMan);
				}
			}).start();
		}

	}

	private static Set<ConfigReader> getTestConfigs() throws SocketException {
		Set<ConfigReader> configs = new HashSet<ConfigReader>();

		{
			String name = "helloNAME";
			UUID uuid = UUID.fromString("12345678-9abc-def0-1234-56789abcdef0");
			int maxAppthreads = 0;

			InetSocketAddress[] listen = new InetSocketAddress[3];
			listen[0] = new InetSocketAddress("localhost", 8888);
			listen[1] = new InetSocketAddress("127.0.0.1", 4242);
			listen[2] = new InetSocketAddress("localhost", 1212);
			InetSocketAddress[] announce = new InetSocketAddress[1];
			announce[0] = new InetSocketAddress("localhost", 8888);
			NetConnection[] multicast = new NetConnection[1];
			multicast[0] = new NetConnection(new InetSocketAddress("255.0.0.1", 1212), NetworkInterface.getByName("lo"));

			Set<BlockTypeHolder> secondLevelChild = new HashSet<BlockTypeHolder>();
			secondLevelChild.add(new BlockTypeHolder("child1TYPE", 2));
			secondLevelChild.add(new BlockTypeHolder("child2TYPE", 2));

			Set<BlockTypeHolder> firstLevelChild = new HashSet<BlockTypeHolder>();
			firstLevelChild.add(new BlockTypeHolder("child2TYPE", 1));

			firstLevelChild.add(new BlockTypeHolder(secondLevelChild, 1));
			BlockTypeHolder allowedBlocks = new BlockTypeHolder(firstLevelChild, 0);

			configs.add(new TestConfigReader(name, uuid, maxAppthreads, true, listen, announce, multicast,
					allowedBlocks));
		}
		{
			String name = "alternateNAME";
			UUID uuid = UUID.fromString("99945678-9abd-def0-1234-56789abcdef0");
			int maxAppthreads = 0;

			InetSocketAddress[] listen = new InetSocketAddress[3];
			listen[0] = new InetSocketAddress("localhost", 8889);
			listen[1] = new InetSocketAddress("127.0.0.1", 4243);
			listen[2] = new InetSocketAddress("localhost", 1213);

			InetSocketAddress[] announce = new InetSocketAddress[1];
			announce[0] = new InetSocketAddress("localhost", 8889);

			NetConnection[] multicast = new NetConnection[1];
			multicast[0] = new NetConnection(new InetSocketAddress("255.0.0.1", 1212), NetworkInterface.getByName("lo"));

			Set<BlockTypeHolder> secondLevelChild = new HashSet<BlockTypeHolder>();
			secondLevelChild.add(new BlockTypeHolder("child1TYPE", 2));
			secondLevelChild.add(new BlockTypeHolder("child2TYPE", 2));

			Set<BlockTypeHolder> firstLevelChild = new HashSet<BlockTypeHolder>();
			firstLevelChild.add(new BlockTypeHolder("child2TYPE", 1));

			firstLevelChild.add(new BlockTypeHolder(secondLevelChild, 1));
			BlockTypeHolder allowedBlocks = new BlockTypeHolder(firstLevelChild, 0);

			configs.add(new TestConfigReader(name, uuid, maxAppthreads, true, listen, announce, multicast,
					allowedBlocks));
		}
		return configs;
	}

}
