package edu.teco.dnd.module;

import java.io.IOException;
import java.io.Serializable;

import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.module.config.JsonConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		Module currentModule;

		if (args.length > 0) {
			LOGGER.debug("argument 0 is \"{}\"", args[0]);
			if (args[0].equals("--help") || args[0].equals("-h")) {
				System.out.println("Parameters: [--help| $pathToConfig]");
				System.out.println("\t --help: print this message");
				System.out.println("\t $pathToConfig the path to the used config file.");
			} else {
				configPath = args[0];
			}
		}

		try {
			currentModule = new Module(configPath);
		} catch (IOException e) {
			LOGGER.fatal("Cannot read config file.");
			LOGGER.catching(e);
			System.exit(2);
		}

		LOGGER.info("Not Loading CommunicationAgent");

	}



}
