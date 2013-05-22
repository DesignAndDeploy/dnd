package edu.teco.dnd.module;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.module.config.JsonConfig;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.TCPConnectionManager;

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
	public static final String DEFAULT_CONFIG_PATH = "./module.cfg";

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
			LOGGER.error("could not load config", e);
			System.exit(1);
		}
		
		
		// TODO: add config options to allow selection of netty engine and number of application threads
		// TODO: name threads
		final NioEventLoopGroup networkEventLoopGroup = new NioEventLoopGroup();
		
		final TCPConnectionManager connectionManager = new TCPConnectionManager(networkEventLoopGroup,
				networkEventLoopGroup,
				new ChannelFactory<NioServerSocketChannel>() {
					@Override
					public NioServerSocketChannel newChannel() {
						return new NioServerSocketChannel();
					}
				},
				new ChannelFactory<NioSocketChannel>() {
					@Override
					public NioSocketChannel newChannel() {
						return new NioSocketChannel();
					}
				},
				moduleConfig.getUuid()
			);
		for (final InetSocketAddress address : moduleConfig.getListen()) {
			connectionManager.startListening(address);
		}
		
		// TODO: start multicast beacon
	}
}
