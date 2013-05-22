package edu.teco.dnd.module;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.module.config.JsonConfig;

public class Module {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Module.class);
	/**
	 * The ModuleConfig describing this module.
	 */
	private ConfigReader moduleConfig = null;

	/**
	 * constructor for a Module with a configuration.
	 * 
	 * @param confPath
	 *            path to the configuration file
	 * @throws IOException
	 *             if config file not readable
	 */
	public Module(final String confPath) throws IOException {
		if (confPath == null) {
			throw new IllegalArgumentException();
		}
		LOGGER.debug("Loading config file ({})", confPath);

		moduleConfig = new JsonConfig(confPath);
	}

	/**
	 * Returns the ModuleConfig for this module.
	 * 
	 * @return the ModuleConfig for this module
	 */
	public ConfigReader getModuleConfig() {
		return moduleConfig;
	}
	
}
