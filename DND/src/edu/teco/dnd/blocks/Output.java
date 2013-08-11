package edu.teco.dnd.blocks;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * An output of a {@link FunctionBlock}.
 * 
 * @param <T>
 *            the type of data this Output outputs
 */
public class Output<T extends Serializable> implements Serializable {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 8652366978996728530L;
	
	public void setValue(final T value) {
		
	}
}
