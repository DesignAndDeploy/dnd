package edu.teco.dnd.blocks;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.Module;

/**
 * <p>
 * A FunctionBlock is a small piece of code that is run by a {@link Module}. FunctionBlocks should be designed so that
 * they either read a single sensor, control a single actor or process the data of one or multiple sensor blocks. Then
 * the blocks can be combined in an {@link Application}.
 * </p>
 * 
 * <p>
 * For building a FunctionBlock, add the DND classes to your project’s class path and extend this class. Then add
 * {@link Input}s, {@link Output}s and options and implement {@link #init(Map)}, {@link #update()} and
 * {@link #shutdown()}.
 * </p>
 * 
 * <p>
 * For Inputs and Outputs it is enough to define a field that uses the desired type as its generic argument. You should
 * never set the field yourself, this will be done by the wrapper code. The fields will not be initialized when your
 * constructor is being run, but as soon as {@link #init(Map)} is called the Inputs and Outputs are available.
 * </p>
 * 
 * <p>
 * For options you have to define a <code>public static final String</code> field. The name must start with
 * <code>OPTION_</code>. The value of the field will be the default value for the option.
 * </p>
 * 
 * <p>
 * There are also two fields that can be used to influence the way the FunctionBlock is updated:
 * {@value #BLOCK_TYPE_FIELD_NAME} and {@value #BLOCK_UPDATE_INTERVAL_FIELD_NAME}. Both fields must be
 * <code>public static final long</code> to be recognized. {@value #BLOCK_TYPE_FIELD_NAME} defines the type of the block
 * which is used during distribution. {@value #BLOCK_UPDATE_INTERVAL_FIELD_NAME} defines an interval in milliseconds.
 * </p>
 * 
 * <p>
 * Note about using this class manually: Before calling most of the methods you'll have to call
 * {@link #initInternal(FunctionBlockID, String)} which initializes the data returned by the other methods.<br />
 * </p>
 */
public abstract class FunctionBlock implements Serializable {
	private static final long serialVersionUID = 7444744469990667015L;

	/**
	 * This is the name of the field that is used to set the block type.
	 */
	public static final String BLOCK_TYPE_FIELD_NAME = "BLOCK_TYPE";

	/**
	 * This is the name of the field that is used to set the update interval.
	 */
	public static final String BLOCK_UPDATE_INTERVAL_FIELD_NAME = "BLOCK_UPDATE_INTERVAL";

	// The following block of fields is initialized by doInit
	private FunctionBlockID blockID = null;
	private String blockType = null;
	private String blockName = null;
	private Long updateInterval = null;
	private Map<String, Output<? extends Serializable>> outputs = null;
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
	 *             if querying a field using reflection fails
	 */
	public final synchronized void initInternal(final FunctionBlockID blockID, final String blockName)
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
	 * Returns the ID of this block. {@link #initInternal(FunctionBlockID, String)} must be called first.
	 * 
	 * @return the ID of this block or null if it hasn't been set yet
	 */
	public final synchronized FunctionBlockID getBlockID() {
		return this.blockID;
	}

	/**
	 * Returns the name of this block. {@link #initInternal(FunctionBlockID, String)} must be called first.
	 * 
	 * @return the name of this block or null if it hasn't been set yet.
	 */
	public final synchronized String getBlockName() {
		return this.blockName;
	}

	/**
	 * Returns all Outputs of the block mapped from their name. {@link #initInternal(FunctionBlockID, String)} must be
	 * called first.
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
	 * Returns all Inputs of the block mapped from their name. {@link #initInternal(FunctionBlockID, String)} must be
	 * called first.
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
	 * Returns the type of the block. {@link #initInternal(FunctionBlockID, String)} must be called first.
	 * 
	 * @return the type of the block
	 */
	public final synchronized String getBlockType() {
		if (blockID == null) {
			throw new IllegalStateException("doInit hasn't been called yet");
		}
		return blockType;
	}

	/**
	 * Returns the update interval for the block. {@link #initInternal(FunctionBlockID, String)} must be called first.
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
			return "FunctionBlock[class=" + getClass() + ",blockID=" + blockID + "]";
		}
	}
}
