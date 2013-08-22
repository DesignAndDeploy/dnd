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
	 * The UUID of the block. Will be set in {@link #doInit(UUID)}. Is used as an indicator to see if doInit has been
	 * called.
	 */
	private UUID blockUUID = null;

	/**
	 * The type of the block. Set in {@link #doInit(UUID)}.
	 */
	private String blockType = null;

	/**
	 * The time between scheduled updates of the block. Set in {@link #doInit(UUID)}.
	 */
	private Long updateInterval = null;

	/**
	 * Contains all outputs of the block mapped from their name.
	 */
	private Map<String, Output<? extends Serializable>> outputs = null;

	/**
	 * Contains all inputs of the block mapped from their name.
	 */
	private Map<String, Input<? extends Serializable>> inputs = null;

	/**
	 * Initializes the block. This method queries the block type and update interval and creates the Inputs and Outputs
	 * of the block.
	 * 
	 * @param blockUUID
	 *            the UUID of the block. Will be stored so that it can be queried later with {@link #getBlockUUID()}.
	 * @throws IllegalAccessException
	 *             if quering a field using reflection fails
	 */
	public final synchronized void doInit(final UUID blockUUID) throws IllegalAccessException {
		if (this.blockUUID != null) {
			return;
		}

		if (blockUUID == null) {
			this.blockUUID = UUID.randomUUID();
		} else {
			this.blockUUID = blockUUID;
		}

		final Map<String, Output<? extends Serializable>> outputs =
				new HashMap<String, Output<? extends Serializable>>();
		final Map<String, Input<? extends Serializable>> inputs = new HashMap<String, Input<? extends Serializable>>();
		for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
			for (final Field field : c.getDeclaredFields()) {
				final Class<?> type = field.getType();
				final String name = field.getName();
				if (Output.class.isAssignableFrom(type) && !outputs.containsKey(name)) {
					final Output<?> output = new Output<Serializable>();
					field.setAccessible(true);
					field.set(this, output);
					outputs.put(name, output);
				} else if (Input.class.isAssignableFrom(type) && !inputs.containsKey(name)) {
					final Input<?> input = new Input<Serializable>();
					field.setAccessible(true);
					field.set(this, input);
					inputs.put(name, input);
				} else if (String.class.isAssignableFrom(type) && blockType == null
						&& "BLOCK_TYPE".equals(field.getName())) {
					field.setAccessible(true);
					blockType = (String) field.get(this);
				} else if (Long.class.isAssignableFrom(type) && updateInterval == null
						&& "BLOCK_UPDATE_INTERVAL".equals(field.getName())) {
					field.setAccessible(true);
					updateInterval = (Long) field.get(this);
				}
			}
		}
		this.outputs = Collections.unmodifiableMap(outputs);
		this.inputs = Collections.unmodifiableMap(inputs);

		if (this.updateInterval == null) {
			this.updateInterval = Long.MIN_VALUE;
		}
	}

	/**
	 * Returns the UUID of this block. {@link #doInit(UUID)} must be called first.
	 * 
	 * @return the UUID of this block or null if it hasn't been set yet
	 */
	public final synchronized UUID getBlockUUID() {
		if (blockUUID == null) {
			throw new IllegalStateException("doInit hasn't been called yet");
		}
		return this.blockUUID;
	}

	/**
	 * Returns all Outputs of the block mapped from their name. {@link #doInit(UUID)} must be called first.
	 * 
	 * @return the Outputs of the block mapped from their name
	 */
	public final synchronized Map<String, Output<? extends Serializable>> getOutputs() {
		if (blockUUID == null) {
			throw new IllegalStateException("doInit hasn't been called yet");
		}
		return this.outputs;
	}

	/**
	 * Returns all Inputs of the block mapped from their name. {@link #doInit(UUID)} must be called first.
	 * 
	 * @return the Inputs of the block mapped from their name
	 */
	public final synchronized Map<String, Input<? extends Serializable>> getInputs() {
		if (blockUUID == null) {
			throw new IllegalStateException("doInit hasn't been called yet");
		}
		return this.inputs;
	}

	/**
	 * Returns the type of the block. {@link #doInit(UUID)} must be called first.
	 * 
	 * @return
	 */
	public final synchronized String getBlockType() {
		if (blockUUID == null) {
			throw new IllegalStateException("doInit hasn't been called yet");
		}
		return blockType;
	}

	/**
	 * Returns the update interval for the block. {@link #doInit(UUID)} must be called first.
	 * 
	 * @return the update interval for the block
	 */
	public final synchronized long getUpdateInterval() {
		if (blockUUID == null) {
			throw new IllegalStateException("doInit hasn't been called yet");
		}
		return updateInterval;
	}

	/**
	 * This method is called when a FunctionBlock is started on a module.
	 */
	public abstract void init();

	/**
	 * Can be used to perform necessary operations upon block shutdown. Block will still be in a functional state,
	 * although it is likely that at least some of the connected blocks have already been shut down. Should return
	 * quickly, otherwise it might be terminated and no other shutdowns of the application executed.
	 */
	public abstract void shutdown();

	/**
	 * This method is called when either the inputs have new values or the timer has run out.
	 */
	public abstract void update();

	@Override
	public final int hashCode() {
		final UUID blockUUID = getBlockUUID();
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blockUUID == null) ? 0 : blockUUID.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionBlock other = (FunctionBlock) obj;
		final UUID blockUUID = getBlockUUID();
		final UUID otherBlockUUID = other.getBlockUUID();
		if (blockUUID == null) {
			if (otherBlockUUID != null)
				return false;
		} else if (!blockUUID.equals(otherBlockUUID))
			return false;
		return true;
	}

	@Override
	public final String toString() {
		final UUID blockUUID = getBlockUUID();
		if (blockUUID == null) {
			return "FunctionBlock[class=" + getClass() + "]";
		} else {
			return "FunctionBlock[class=" + getClass() + ",blockUUID=" + blockUUID + "]";
		}
	}
}
