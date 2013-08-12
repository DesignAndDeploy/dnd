package edu.teco.dnd.blocks;

import java.io.Serializable;

/**
 * Base class function blocks. Subclasses have to implement {@link #init()} and
 * {@link #update()}.
 * 
 * @see Input
 * @see Output
 * @see Option
 * @see Timed
 */
public abstract class FunctionBlock implements Serializable {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 7444744469990667015L;

	/**
	 * This method is called when a FunctionBlock is started on a module.
	 */
	public abstract void init();
	
	public abstract void update();
}
