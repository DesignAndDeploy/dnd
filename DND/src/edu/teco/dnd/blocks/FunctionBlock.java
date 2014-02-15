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
 */
public abstract class FunctionBlock implements Serializable {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 7444744469990667015L;

	private static final String BLOCK_TYPE_FIELD_NAME = "BLOCK_TYPE";

	private static final String BLOCK_UPDATE_INTERVAL_FIELD_NAME = "BLOCK_UPDATE_INTERVAL";

	/**
	 * The ID of the block. Will be set in {@link #doInit(FunctionBlockID, String)}. Is used as an indicator to see if
	 * doInit has been called.
	 */
	private FunctionBlockID blockID = null;

	/**
	 * The type of the block. Set in {@link #doInit(UUID, String)}.
	 */
	private String blockType = null;

	/**
	 * The name of the block. Set in {@link #doInit(UUID, String)}.
	 */
	private String blockName = null;

	/**
	 * The time between scheduled updates of the block. Set in {@link #doInit(UUID, String)}.
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
	 * @param blockID
	 *            the ID of the block. Will be stored so that it can be queried later with {@link #getBlockID()}. A
	 *            random value is used if null.
	 * @param blockName
	 *            the Name of the block. Will be stored so that it can be queried later with {@link #getBlockName()}.
	 * @throws IllegalAccessException
	 *             if quering a field using reflection fails
	 */
	public final synchronized void doInit(final FunctionBlockID blockID, final String blockName)
			throws IllegalAccessException {
		if (this.blockID != null) {
			return;
		}
		this.blockName = blockName;

		if (blockID == null) {
			this.blockID = new FunctionBlockID();
		} else {
			this.blockID = blockID;
		}

		final Map<String, Output<? extends Serializable>> outputs =
				new HashMap<String, Output<? extends Serializable>>();
		final Map<String, Input<? extends Serializable>> inputs = new HashMap<String, Input<? extends Serializable>>();
		for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
			for (final Field field : c.getDeclaredFields()) {
				final Class<?> type = field.getType();
				final String name = field.getName();
				if (isOutput(type) && !outputs.containsKey(name)) {
					outputs.put(name, createOutput(field));
				} else if (isInput(type) && !inputs.containsKey(name)) {
					inputs.put(name, createInput(field));
				} else if (isBlockType(field) && blockType == null) {
					blockType = (String) getFieldValue(field);
				} else if (isUpdateInterval(field) && updateInterval == null) {
					updateInterval = (Long) getFieldValue(field);
				}
			}
		}
		this.outputs = Collections.unmodifiableMap(outputs);
		this.inputs = Collections.unmodifiableMap(inputs);

		if (this.updateInterval == null) {
			this.updateInterval = Long.MIN_VALUE;
		}
	}

	private static final boolean isOutput(final Class<?> cls) {
		return Output.class.isAssignableFrom(cls);
	}

	private final Output<?> createOutput(Field field) throws IllegalArgumentException, IllegalAccessException {
		final Output<?> output = new Output<Serializable>();
		field.setAccessible(true);
		field.set(this, output);
		return output;
	}

	private static final boolean isInput(final Class<?> cls) {
		return Input.class.isAssignableFrom(cls);
	}

	private final Input<?> createInput(final Field field) throws IllegalArgumentException, IllegalAccessException {
		final Input<?> input = new Input<Serializable>();
		field.setAccessible(true);
		field.set(this, input);
		return input;
	}

	private boolean isBlockType(final Field field) {
		return isString(field.getType()) && BLOCK_TYPE_FIELD_NAME.equals(field.getName());
	}

	private static final boolean isString(final Class<?> cls) {
		return String.class.isAssignableFrom(cls);
	}

	private boolean isUpdateInterval(Field field) {
		return isLong(field.getType()) && BLOCK_UPDATE_INTERVAL_FIELD_NAME.equals(field.getName());
	}

	private boolean isLong(final Class<?> cls) {
		return Long.class.isAssignableFrom(cls) || long.class.isAssignableFrom(cls);
	}

	private final Object getFieldValue(final Field field) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		return field.get(this);
	}

	/**
	 * Returns the UUID of this block. {@link #doInit(UUID, String)} must be called first.
	 * 
	 * @return the UUID of this block or null if it hasn't been set yet
	 */
	public final synchronized FunctionBlockID getBlockID() {
		return this.blockID;
	}

	/**
	 * Returns the name of this block. {@link #doInit(UUID, String)} must be called first.
	 * 
	 * @return the name of this block or null if it hasn't been set yet.
	 */
	public final synchronized String getBlockName() {
		return this.blockName;
	}

	/**
	 * Returns all Outputs of the block mapped from their name. {@link #doInit(UUID, String)} must be called first.
	 * 
	 * @return the Outputs of the block mapped from their name
	 */
	public final synchronized Map<String, Output<? extends Serializable>> getOutputs() {
		if (blockID == null) {
			throw new IllegalStateException("doInit hasn't been called yet");
		}
		return this.outputs;
	}

	/**
	 * Returns all Inputs of the block mapped from their name. {@link #doInit(UUID, String)} must be called first.
	 * 
	 * @return the Inputs of the block mapped from their name
	 */
	public final synchronized Map<String, Input<? extends Serializable>> getInputs() {
		if (blockID == null) {
			throw new IllegalStateException("doInit hasn't been called yet");
		}
		return this.inputs;
	}

	/**
	 * Returns the type of the block. {@link #doInit(UUID, String)} must be called first.
	 * 
	 * @return
	 */
	public final synchronized String getBlockType() {
		if (blockID == null) {
			throw new IllegalStateException("doInit hasn't been called yet");
		}
		return blockType;
	}

	/**
	 * Returns the update interval for the block. {@link #doInit(UUID, String)} must be called first.
	 * 
	 * @return the update interval for the block
	 */
	public final synchronized long getUpdateInterval() {
		if (blockID == null) {
			throw new IllegalStateException("doInit hasn't been called yet");
		}
		return updateInterval;
	}

	/**
	 * This method is called when a FunctionBlock is started on a module.
	 * 
	 * @param options
	 *            a Map from an Option's name to its value
	 */
	public abstract void init(final Map<String, String> options);

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
		final FunctionBlockID blockID = getBlockID();
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blockID == null) ? 0 : blockID.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FunctionBlock other = (FunctionBlock) obj;
		final FunctionBlockID blockID = getBlockID();
		final FunctionBlockID otherBlockID = other.getBlockID();
		if (blockID == null) {
			if (otherBlockID != null) {
				return false;
			}
		} else if (!blockID.equals(otherBlockID)) {
			return false;
		}
		return true;
	}

	@Override
	public final String toString() {
		final FunctionBlockID blockID = getBlockID();
		if (blockID == null) {
			return "FunctionBlock[class=" + getClass() + "]";
		} else {
			return "FunctionBlock[class=" + getClass() + ",blockUUID=" + blockID + "]";
		}
	}
}
