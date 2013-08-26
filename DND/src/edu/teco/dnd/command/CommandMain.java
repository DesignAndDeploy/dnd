package edu.teco.dnd.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.netty.util.internal.logging.InternalLoggerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.deploy.Constraint;
import edu.teco.dnd.deploy.Distribution;
import edu.teco.dnd.deploy.DistributionGenerator;
import edu.teco.dnd.deploy.MinimalModuleCountEvaluator;
import edu.teco.dnd.deploy.UserConstraints;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.network.logging.Log4j2LoggerFactory;
import edu.teco.dnd.server.DistributionCreator;
import edu.teco.dnd.server.ModuleManager;
import edu.teco.dnd.server.NoBlocksException;
import edu.teco.dnd.server.NoModulesException;
import edu.teco.dnd.server.ServerManager;

/**
 * This class offers the option to create and deploy a distribution of an already existing application via command line.
 * The addresses for multicast, announce and listen are passed as commands, same as the path of the application to
 * distribute. In addition, the user can determine whether he just wants to create a distribution and have it displayed
 * or whether he also wants do directly deploy the distribution.
 * 
 * @author jung
 * 
 */
public class CommandMain {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(CommandMain.class);

	/**
	 * Use this if the user only wants to create a distribution.
	 */
	public static final String ONLY_CREATE = "create";

	/**
	 * Use this if the user wants to create and directly deploy any distribution.
	 */
	public static final String CREATE_AND_DEPLOY = "deploy";

	/**
	 * Identifier for the announce address, to be entered directly before the address.
	 */
	public static final String ANNOUNCE = "--announce";

	/**
	 * Identifier for the multicast address, to be entered directly before the address.
	 */
	public static final String MULTICAST = "--multicast";

	/**
	 * Identifier for the listen address, to be entered directly before the address.
	 */
	public static final String LISTEN = "--listen";

	/**
	 * Identifier for the path of the .blocks file representing the application to distribute. To be entered directly
	 * before the path.
	 */
	public static final String PATH = "--path";

	/**
	 * Identifier to determine that the program shall create a distribution and directly deploy the application
	 * according to the distribution. Use either this or CREATE, don't use both.
	 */
	public static final String DEPLOY = "--deploy";

	/**
	 * Identifier to determine that the program shall only create and display a possible distribution. Use either this
	 * or DEPLOY, don't use both.
	 */
	public static final String CREATE = "--create";

	private static String path;
	private static String multicast;
	private static String announce;
	private static String listen;
	private static String createOrDeploy;

	private static Collection<FunctionBlockModel> functionBlocks;
	private static ServerManager serverManager;

	/**
	 * The main method.
	 * 
	 * @param args
	 *            Arguments for the command line program. Must contain addresses for announce, multicast and listen, a
	 *            classpath for a .blocks file and either --create or --deploy to determine whether to only create or
	 *            also deploy a distribution.
	 */
	public static void main(String args[]) {
		InternalLoggerFactory.setDefaultFactory(new Log4j2LoggerFactory());

		if (args.length > 0) {
			LOGGER.debug("argument 0 is \"{}\"", args[0]);
			if (args[0].equals("--help") || args[0].equals("-h")) {
				System.out.println("Parameters: [--help| $pathToConfig]");
				System.out.println("\t--help: print this message");
				System.out.println("\t$pathToConfig the path to the used config file.");
				System.exit(0);
			} else {
				parseArguments(args);
			}
		} else {
			exitFalseInput();
		}

		// FunctionBlockLoader blockLoader = new FunctionBlockLoader(path);
		// functionBlocks = blockLoader.getBlocks();

		// TODO: What happens when you call startServer() while the server is already running? Bad or no problem? Is
		// this check necessary?
		if (!ServerManager.getDefault().isRunning()) {
			ServerManager.getDefault().startServer(multicast, listen, announce);
		}

		ModuleRegistrator moduleRegistrator = new ModuleRegistrator();
		ServerManager.getDefault().getModuleManager().addModuleManagerListener(moduleRegistrator);

		CommandLoop loop = new CommandLoop(functionBlocks);
		loop.loop(createOrDeploy);

		exit();

	}

	private static void parseArguments(String[] args) {
		boolean pathInput = false;
		boolean multicastInput = false;
		boolean announceInput = false;
		boolean listenInput = false;
		boolean createOrDeployInput = false;

		int i = 0;
		for (; i < args.length - 1; i++) {
			if (args[i].equals(ANNOUNCE)) {
				if (announceInput) {
					exitTooMany(ANNOUNCE);
				}
				announceInput = true;
				i++;
				announce = args[i];
			} else if (args[i].equals(LISTEN)) {
				if (listenInput) {
					exitTooMany(LISTEN);
				}
				listenInput = true;
				i++;
				listen = args[i];
			} else if (args[i].equals(MULTICAST)) {
				if (multicastInput) {
					exitTooMany(MULTICAST);
				}
				multicastInput = true;
				i++;
				multicast = args[i];
			} else if (args[i].equals(PATH)) {
				if (pathInput) {
					exitTooMany(PATH);
				}
				pathInput = true;
				i++;
				path = args[i];
			} else if (args[i].equals(CREATE)) {
				if (createOrDeployInput) {
					exitTooMany("options whether to create or deploy a distribution");
				}
				createOrDeployInput = true;
				createOrDeploy = CommandLoop.CREATE;

			} else if (args[i].equals(DEPLOY)) {
				if (createOrDeployInput) {
					exitTooMany("options whether to create or deploy a distribution");
				}
				createOrDeployInput = true;
				createOrDeploy = CommandLoop.DEPLOY;

			} else {
				exitFalseInput();
			}
		}

		if (i == args.length - 1 && !createOrDeployInput) {
			if (args[args.length - 1].equals(CREATE)) {
				createOrDeployInput = true;
				createOrDeploy = CommandLoop.CREATE;
			} else if (args[args.length - 1].equals(DEPLOY)) {
				createOrDeployInput = true;
				createOrDeploy = CommandLoop.DEPLOY;
			} else {
				System.out.println("Something went wrong with your arguments.");
				System.exit(1);
			}
		}

		if (!(pathInput && multicastInput && listenInput && announceInput)) {
			exitFalseInput();
		}
	}

	private static void exitTooMany(String doubleDefined) {
		System.out.println("You defined more than one " + doubleDefined + ".");
		System.exit(1);
	}

	private static void exitFalseInput() {
		System.out.println("Please define your input by " + ANNOUNCE + " for the announce address, " + LISTEN
				+ " for the listen address, " + MULTICAST + " for the multicast address, " + PATH
				+ " for the path of the blocks. Also use either " + CREATE + " or " + DEPLOY
				+ " to define whether to only create or also deploy a distribution.");
		System.exit(1);
	}

	/**
	 * Called whenever Program is closed regularly (by user or because there's nothing more to do).
	 */
	private static void exit() {
		if (serverManager != null) {
			serverManager.shutdownServer();
		}
	}

}
