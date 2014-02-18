package edu.teco.dnd.module;

import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.module.config.JsonConfig;
import edu.teco.dnd.module.messages.generalModule.MissingApplicationHandler;
import edu.teco.dnd.module.messages.generalModule.ShutdownModuleHandler;
import edu.teco.dnd.module.messages.generalModule.ShutdownModuleMessage;
import edu.teco.dnd.module.messages.infoReq.RequestApplicationInformationMessage;
import edu.teco.dnd.module.messages.infoReq.RequestApplicationInformationMessageHandler;
import edu.teco.dnd.module.messages.infoReq.RequestModuleInfoMessage;
import edu.teco.dnd.module.messages.infoReq.RequestModuleInfoMsgHandler;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationMessage;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationMessageHandler;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationMessage;
import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.module.messages.loadStartBlock.BlockMessage;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassMessage;
import edu.teco.dnd.module.messages.values.ValueMessage;
import edu.teco.dnd.module.messages.values.ValueMessageAdapter;
import edu.teco.dnd.module.messages.values.WhoHasBlockMessage;
import edu.teco.dnd.module.permissions.ApplicationSecurityManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.logging.Log4j2LoggerFactory;
import edu.teco.dnd.network.tcp.TCPConnectionManager;
import edu.teco.dnd.server.TCPUDPServerManager;
import edu.teco.dnd.util.FutureNotifier;

/**
 * The main class that is started on a ModuleInfo.
 */
public final class ModuleMain {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ModuleMain.class);

	/**
	 * Default path for config file.
	 */
	public static final String DEFAULT_CONFIG_PATH = "module.cfg";

	/**
	 * Should never be instantiated.
	 */
	private ModuleMain() {
	}

	/**
	 * @param args
	 *            ;-)
	 */
	public static void main(final String[] args) {
		InternalLoggerFactory.setDefaultFactory(new Log4j2LoggerFactory());

		String configPath = DEFAULT_CONFIG_PATH;
		if (args.length > 0) {
			LOGGER.debug("argument 0 is \"{}\"", args[0]);
			if ("--help".equals(args[0]) || "-h".equals(args[0])) {
				System.out.println("Parameters: [--help| $pathToConfig]");
				System.out.println("\t--help: print this message");
				System.out.println("\t$pathToConfig the path to the used config file.");
				System.exit(0);
			} else {
				configPath = args[0];
			}
		}

		final ConfigReader moduleConfig = getModuleConfig(configPath);
		if (moduleConfig == null) {
			System.exit(1);
		}
		final TCPUDPServerManager serverManager = new TCPUDPServerManager();
		final FutureNotifier<?> serverFuture =
				serverManager.startServer(new ConfigReaderAddressBasedServerConfigAdapter(moduleConfig));

		try {
			System.setSecurityManager(new ApplicationSecurityManager());
		} catch (SecurityException se) {
			LOGGER.fatal("Can not set SecurityManager.");
			// FIXME: just calling exit is probably a bad idea
			System.exit(-1);
		}

		final Runnable shutdownHook = new Runnable() {
			@Override
			public void run() {
				synchronized (ModuleMain.class) {
					serverManager.shutdownServer();
				}
			}
		};

		try {
			serverFuture.await();
		} catch (final InterruptedException e) {
			LOGGER.error("got interrupted waiting for the server to start");
			System.exit(-1);
		}

		synchronized (ModuleMain.class) {
			Module module = null;
			try {
				module = new Module(moduleConfig, serverManager.getConnectionManager(), shutdownHook);
			} catch (final NoSuchAlgorithmException e) {
				System.err.println("Missing algorithm: " + e);
				System.exit(1);
			}
			registerAdditionalAdapters(serverManager.getConnectionManager(), module);
			registerHandlers(serverManager.getConnectionManager(), moduleConfig, module);
		}

		System.out.println("Module is up and running.");
	}

	/**
	 * read the configuration file into a ConfigReader.
	 * 
	 * @param configPath
	 *            path to configuration file
	 * @return a configReader with the read configuration.
	 */
	private static ConfigReader getModuleConfig(final String configPath) {
		ConfigReader moduleConfig = null;
		try {
			moduleConfig = new JsonConfig(configPath);
		} catch (IOException e) {
			LOGGER.warn("could not open file: \"{}\"", configPath);
			return null;
		} catch (Exception e) {
			LOGGER.warn("could not load config: \"{}\"", configPath);
			e.printStackTrace();
			return null;
		}
		return moduleConfig;
	}

	private static void registerAdditionalAdapters(final TCPConnectionManager tcpConnectionManager, final Module module) {
		tcpConnectionManager.registerTypeAdapter(ValueMessage.class, new ValueMessageAdapter(module));
	}

	private static void registerHandlers(ConnectionManager connectionManager, ConfigReader moduleConfig, Module module) {
		connectionManager.addHandler(JoinApplicationMessage.class, new JoinApplicationMessageHandler(module));
		connectionManager.addHandler(RequestApplicationInformationMessage.class,
				new RequestApplicationInformationMessageHandler(moduleConfig.getModuleID(), module));
		connectionManager.addHandler(RequestModuleInfoMessage.class, new RequestModuleInfoMsgHandler(moduleConfig));

		connectionManager.addHandler(LoadClassMessage.class, new MissingApplicationHandler());
		connectionManager.addHandler(BlockMessage.class, new MissingApplicationHandler());
		connectionManager.addHandler(StartApplicationMessage.class, new MissingApplicationHandler());
		connectionManager.addHandler(KillAppMessage.class, new MissingApplicationHandler());
		connectionManager.addHandler(ValueMessage.class, new MissingApplicationHandler());
		connectionManager.addHandler(WhoHasBlockMessage.class, new MissingApplicationHandler());
		connectionManager.addHandler(ShutdownModuleMessage.class, new ShutdownModuleHandler(module));
	}
}
