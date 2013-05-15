package edu.teco.dnd.module;

import java.io.IOException;

/**
 * Abstract interface every configfile must provide.
 */
public interface ConfigFile {

	/**
	 * returns a config option indexed by the given key.
	 * 
	 * @param key
	 *            the name of the config option
	 * @return the configuration option
	 */
	String getProperty(String key);

	/**
	 * sets a given configuration option "key" to "value".
	 * 
	 * @param key
	 *            the name of the config option
	 * @param value
	 *            the value the option is to be set to.
	 */
	void setProperty(String key, String value);

	/**
	 * @throws IOException
	 *             write the configuration. (Probably written to disk, but this is not necessarily the case.)
	 */
	void save() throws IOException;
}
