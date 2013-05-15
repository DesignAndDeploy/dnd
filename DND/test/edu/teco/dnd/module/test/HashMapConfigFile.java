package edu.teco.dnd.module.test;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import edu.teco.dnd.module.ConfigFile;

/**
 * A ConfigFile implementation using a HashMap for storing the values. Used for testing.
 */
public class HashMapConfigFile implements ConfigFile, Serializable {
	/**
	 * The values that have been stored.
	 */
	private HashMap<String, String> values = new HashMap<>();

	/**
	 * Used to make sure that save has been called.
	 */
	private boolean saved = false;

	@Override
	public final String getProperty(final String key) {
		return values.get(key);
	}

	@Override
	public final void setProperty(final String key, final String value) {
		values.put(key, value);
	}

	@Override
	public final void save() throws IOException {
		saved = true;
	}

	/**
	 * Returns whether or not {@link #save()} has been called since the last time {@link #resetSavedState()}
	 * has been called.
	 * 
	 * @return whether or not save() has been called
	 */
	public final boolean hasBeenSaved() {
		return saved;
	}

	/**
	 * Resets the save state.
	 */
	public final void resetSavedState() {
		saved = false;
	}
}
