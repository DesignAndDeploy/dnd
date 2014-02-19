package edu.teco.dnd.blocks;

import java.io.Serializable;

/**
 * Something that can receive values from an {@link Output}.
 * 
 * @param <T>
 *            the types of values that can be received by this object
 */
public interface OutputTarget<T extends Serializable> {
	/**
	 * Receives a value from an Output.
	 * 
	 * @param value
	 *            the value sent by the Output.
	 */
	void setValue(T value);
}
