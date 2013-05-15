package edu.teco.dnd.module;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * Configuration file using java properties.
 */
public class PropertiesConfigFile implements ConfigFile, Serializable {

	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 8894216946419123486L;

	/** A copy of the properties file which is stored on the disk of a module. */
	private Properties props;

	/** The path to the stored properties file. */
	private String path;

	/**
	 * Reads the configuration from Path and constructs the Object with this.
	 * 
	 * @param path
	 *            the path to the file
	 * @throws IOException
	 *             Constructor taking a file path.
	 * 
	 */
	public PropertiesConfigFile(final String path) throws IOException {
		this.path = path;
		props = new Properties();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(path));
		props.load(stream);
		stream.close();
	}

	/**
	 * Returns the Properties of a key.
	 * 
	 * @param key
	 *            Key to return properties from.
	 * @return properties of a key.
	 */
	@Override
	public String getProperty(final String key) {
		return props.getProperty(key);
	}

	/**
	 * Sets properties of a key.
	 * 
	 * @param key
	 *            key to set properties
	 * @param value
	 *            value to be set
	 */
	@Override
	public void setProperty(final String key, final String value) {
		props.setProperty(key, value);
	}

	@Override
	public void save() throws IOException {
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
		props.store(stream, null);
		stream.close();
	}

}
