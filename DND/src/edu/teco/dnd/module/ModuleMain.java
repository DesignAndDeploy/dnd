package edu.teco.dnd.module;

import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.config.JsonConfigFactory;
import edu.teco.dnd.module.config.ModuleConfig;
import edu.teco.dnd.module.config.ModuleConfigFactory;
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
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.logging.Log4j2LoggerFactory;
import edu.teco.dnd.network.tcp.TCPConnectionManager;
import edu.teco.dnd.server.TCPUDPServerManager;
import edu.teco.dnd.util.FutureNotifier;

/**
 * Startup code for a {@link Module}.
 */
public final class ModuleMain {
	private static final Logger LOGGER = LogManager.getLogger(ModuleMain.class);

	/**
	 * Default path used to load the configuration file. Can be changed by passing the name of a different configuration
	 * file on the command line.
	 */
	public static final URI DEFAULT_CONFIG_URI = new File("module.cfg").getAbsoluteFile().toURI();

	private static final ModuleConfigFactory MODULE_CONFIG_FACTORY = new JsonConfigFactory();

	/**
	 * This class shouldn't be instantiated, so the constructor is private.
	 */
	private ModuleMain() {
	}

	/**
	 * Starts a {@link Module}. It will load {@link JsonConfig}, start up a {@link TCPUDPServerManager} and register the
	 * necessary {@link MessageHandler}s.
	 * 
	 * @param args
	 *            Command line parameters. Either pass <code>-h</code> or <code>--help</code> for help or pass a single
	 *            file name that will be used as the path to the configuration file instead of
	 *            {@link #DEFAULT_CONFIG_PATH}.
	 */
	public static void main(final String[] args) {
		InternalLoggerFactory.setDefaultFactory(new Log4j2LoggerFactory());

		URI configURI = DEFAULT_CONFIG_URI;
		if (args.length > 0) {
			LOGGER.debug("argument 0 is \"{}\"", args[0]);
			if ("--help".equals(args[0]) || "-h".equals(args[0])) {
				System.out.println("Parameters: [--help| $pathToConfig]");
				System.out.println("\t--help: print this message");
				System.out.println("\t$pathToConfig the path to the used config file.");
				System.exit(0);
			} else {
				try {
					configURI = URI.create(args[0]);
				} catch (final IllegalArgumentException e) {
					configURI = new File(args[0]).getAbsoluteFile().toURI();
				}
			}
		}

		ModuleConfig moduleConfig = null;
		try {
			moduleConfig = MODULE_CONFIG_FACTORY.loadConfiguration(configURI);
		} catch (IOException e) {
			LOGGER.warn("could not load configuration from {}", configURI);
			System.exit(1);
		}

		final TCPUDPServerManager serverManager = new TCPUDPServerManager();
		final FutureNotifier<?> serverFuture =
				serverManager.startServer(new ModuleConfigAddressBasedServerConfigAdapter(moduleConfig));

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
	 * Registers type adapters that are only used by {@link Module}s.
	 * 
	 * @param tcpConnectionManager
	 *            the TCPConnectionManager the adapters should be registered with
	 * @param module
	 *            the Module that should be used
	 */
	private static void registerAdditionalAdapters(final TCPConnectionManager tcpConnectionManager, final Module module) {
		tcpConnectionManager.registerTypeAdapter(ValueMessage.class, new ValueMessageAdapter(module));
	}

	/**
	 * Registers {@link MessageHandler}s used by {@link Module}.
	 * 
	 * @param connectionManager
	 *            the {@link ConnectionManager} the handlers should be registered with
	 * @param moduleConfig
	 *            the configuration used by the Module
	 * @param module
	 *            the Module
	 */
	private static void registerHandlers(ConnectionManager connectionManager, ModuleConfig moduleConfig, Module module) {
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
