package edu.teco.dnd.blocks;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
	
	private Map<String, Output<? extends Serializable>> outputs = null;
	
	public synchronized final void doInit(final UUID blockUUID) throws IllegalArgumentException, IllegalAccessException {
		if (this.blockUUID != null) {
			return;
		}
		
		this.blockUUID = blockUUID;
		final Map<String, Output<? extends Serializable>> outputs = new HashMap<String, Output<? extends Serializable>>();
		for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
			for (final Field field : c.getFields()) {
				if (Output.class.isAssignableFrom(field.getType()) && !outputs.containsKey(field.getName())) {
					final Output<?> output = new Output<Serializable>();
					field.setAccessible(true);
					field.set(this, output);
					outputs.put(field.getName(), output);
				}
			}
		}
		this.outputs = Collections.unmodifiableMap(outputs);
	}

	/**
	 * Returns the UUID of this block.
	 * 
	 * @return the UUID of this block or null if it hasn't been set yet
	 */
	public final synchronized UUID getBlockUUID() {
		return this.blockUUID;
	}
	
	public final synchronized Map<String, Output<? extends Serializable>> getOutputs() {
		return this.outputs;
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
