package edu.teco.dnd.module;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioDatagramChannel;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.module.config.JsonConfig;
import edu.teco.dnd.module.messages.StartAppMessage;
import edu.teco.dnd.module.messages.StartAppMessageHandler;
import edu.teco.dnd.network.TCPConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.network.logging.Log4j2LoggerFactory;
import edu.teco.dnd.util.NetConnection;

/**
 * The main class that is started on a Module.
 */
public class ModuleMain {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ModuleMain.class);

	/**
	 * Default path for config file.
	 */
	public static final String DEFAULT_CONFIG_PATH = "module.cfg";

	public static void main(final String[] args) {
		String configPath = DEFAULT_CONFIG_PATH;

		if (args.length > 0) {
			LOGGER.debug("argument 0 is \"{}\"", args[0]);
			if (args[0].equals("--help") || args[0].equals("-h")) {
				System.out.println("Parameters: [--help| $pathToConfig]");
				System.out.println("\t--help: print this message");
				System.out.println("\t$pathToConfig the path to the used config file.");
			} else {
				configPath = args[0];
			}
		}

		ConfigReader moduleConfig = null;
		try {
			moduleConfig = new JsonConfig(configPath);
		} catch (IOException e) {
			LOGGER.fatal("could not load config", e);
			System.exit(1);
		}

		InternalLoggerFactory.setDefaultFactory(new Log4j2LoggerFactory());

		// TODO: add config options to allow selection of netty engine and
		// number of application threads
		// TODO: name threads
		final NioEventLoopGroup networkEventLoopGroup = new NioEventLoopGroup();

		final TCPConnectionManager connectionManager = new TCPConnectionManager(networkEventLoopGroup,
				networkEventLoopGroup, new ChannelFactory<NioServerSocketChannel>() {
					@Override
					public NioServerSocketChannel newChannel() {
						return new NioServerSocketChannel();
					}
				}, new ChannelFactory<NioSocketChannel>() {
					@Override
					public NioSocketChannel newChannel() {
						return new NioSocketChannel();
					}
				}, moduleConfig.getUuid());
		for (final InetSocketAddress address : moduleConfig.getListen()) {
			connectionManager.startListening(address);
		}

		final UDPMulticastBeacon beacon = new UDPMulticastBeacon(new ChannelFactory<OioDatagramChannel>() {
			@Override
			public OioDatagramChannel newChannel() {
				return new OioDatagramChannel();
			}
		}, new OioEventLoopGroup(), networkEventLoopGroup, moduleConfig.getUuid());
		beacon.addListener(connectionManager);
		beacon.setAnnounceAddresses(Arrays.asList(moduleConfig.getAnnounce()));
		for (final NetConnection address : moduleConfig.getMulticast()) {
			beacon.addAddress(address.getInterface(), address.getAddress());
		}

		ModuleApplicationManager appMan = new ModuleApplicationManager(moduleConfig.getMaxThreads(),
				moduleConfig.getMinAppThreads(), moduleConfig.getUuid(), moduleConfig, connectionManager);

		connectionManager.addHandler(StartAppMessage.class, new StartAppMessageHandler(appMan));

	}

	// TODO: add method for shutdown
}
