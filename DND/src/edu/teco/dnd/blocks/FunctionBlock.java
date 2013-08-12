package edu.teco.dnd.blocks;

import java.io.Serializable;
import java.util.UUID;

/**
 * Base class function blocks. Subclasses have to implement {@link #init()} and {@link #update()}.
 * 
 * @see Input
 * @see Output
 * @see Option
 */
public abstract class FunctionBlock implements Serializable {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 7444744469990667015L;
	
	/**
	 * The UUID of this block. Can't be changed once set.
	 */
	private UUID blockUUID = null;
	
	/**
	 * Sets the block UUID if none has been set. Does not change the UUID once it has been set.
	 * 
	 * @param blockID the UUID of this block
	 * @return true if the block UUID was set, false otherwise
	 */
	public synchronized boolean setBlockUUID(final UUID blockID) {
		if (this.blockUUID != null) {
			return false;
		}
		this.blockUUID = blockID;
		return true;
	}
	
	/**
	 * Returns the UUID of this block.
	 * 
	 * @return the UUID of this block or null if it hasn't been set yet
	 */
	public synchronized UUID getBlockUUID() {
		return this.blockUUID;
	}

	/**
	 * This method is called when a FunctionBlock is started on a module.
	 */
	public abstract void init();
	
	/**
	 * This method is called when either the inputs have new values or the timer has run out.
	 */
	public abstract void update();
}
