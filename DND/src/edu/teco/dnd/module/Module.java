package edu.teco.dnd.module;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Module {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Module.class);
	
	/**
	 * The UUID of this module.
	 */
	private final UUID uuid;
	
	/**
	 * The name of this module.
	 */
	private final String name;

	/**
	 * Initializes a new Module.
	 * 
	 * @param uuid the UUID of the module
	 * @param name the name of the module
	 */
	public Module(final UUID uuid, final String name) {
		LOGGER.entry(uuid, name);
		this.uuid = uuid;
		this.name = name;
		LOGGER.exit();
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
}
