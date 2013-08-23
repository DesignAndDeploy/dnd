package edu.teco.dnd.command;

import java.util.Collection;

import io.netty.util.internal.logging.InternalLoggerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.network.logging.Log4j2LoggerFactory;

public class CommandMain {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(CommandMain.class);

	public static final int ONLYCREATE = 0;
	public static final int CREATEANDDEPLOY = 1;

	public static final String ANNOUNCE = "--announce";
	public static final String MULTICAST = "--multicast";
	public static final String LISTEN = "--listen";
	public static final String PATH = "--path";
	public static final String DEPLOY = "--deploy";
	public static final String CREATE = "--create";

	private static boolean pathInput = false;
	private static boolean multicastInput = false;
	private static boolean announceInput = false;
	private static boolean listenInput = false;
	private static boolean createOrDeployInput = false;

	private static String path;
	private static String multicast;
	private static String announce;
	private static String listen;
	private static int createOrDeploy;
	
	private static Collection<FunctionBlockModel> functionBlocks;

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

		FunctionBlockLoader blockLoader = new FunctionBlockLoader(path);
		functionBlocks = blockLoader.getBlocks();
		
		ServerManager serverManager = new ServerManager(multicast, listen, announce);
		if (!serverManager.isRunning()) {
			serverManager.startServer();
		}
		if (serverManager.isRunning()) {
			System.out.println("Server running");
		} else {
			System.out.println("Server not running");
		}

		
		
		serverManager.shutdownServer();

	}

	private static void parseArguments(String[] args) {
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
				if (multicastInput){
					exitTooMany(MULTICAST);
				}
				multicastInput = true;
				i++;
				multicast = args[i];
			} else if (args[i].equals(PATH)) {
				if (pathInput){
					exitTooMany(PATH);
				}
				pathInput = true;
				i++;
				path = args[i];
			} else if (args[i].equals(CREATE)) {
				if (createOrDeployInput){
					exitTooMany("options whether to create or deploy a distribution");
				}
				createOrDeployInput = true;
				createOrDeploy = ONLYCREATE;

			} else if (args[i].equals(DEPLOY)) {
				if (createOrDeployInput){
					exitTooMany("options whether to create or deploy a distribution");
				}
				createOrDeployInput = true;
				createOrDeploy = CREATEANDDEPLOY;

			} else {
				exitFalseInput();
			}
		}

		if (i == args.length - 1 && !createOrDeployInput) {
			if (args[args.length - 1].equals(CREATE)) {
				createOrDeployInput = true;
				createOrDeploy = ONLYCREATE;
			} else if (args[args.length - 1].equals(DEPLOY)) {
				createOrDeployInput = true;
				createOrDeploy = CREATEANDDEPLOY;
			}
			else{
				System.out.println("Something went wrong with your arguments.");
				System.exit(1);
			}
		}

		if (!(pathInput && multicastInput && listenInput && announceInput && createOrDeployInput)) {
			exitFalseInput();
		}
	}

	private static void exitTooMany(String doubleDefined) {
		System.out.println("You defined more than one " + doubleDefined + ".");
		System.exit(1);
	}
	
	private static void exitFalseInput(){
		System.out.println("Please define your input by " + ANNOUNCE + " for the announce address, " + LISTEN
				+ " for the listen address, " + MULTICAST + " for the multicast address, " + PATH
				+ " for the path of the blocks. Also use either " + CREATE + " or " + DEPLOY
				+ " to define whether to only create or also deploy a distribution.");
		System.exit(1);
	}

}
