package edu.teco.dnd.blocks;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
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

	private Map<String, Input<? extends Serializable>> inputs = null;

	// FIXME: should probably be run in the Constructor so that the block can't execute code beforehand. What about
	// (static) code blocks?
	public synchronized final void doInit(final UUID blockUUID) throws IllegalArgumentException, IllegalAccessException {
		if (this.blockUUID != null) {
			return;
		}

		this.blockUUID = blockUUID;
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
				}
			}
		}
		this.outputs = Collections.unmodifiableMap(outputs);
		this.inputs = Collections.unmodifiableMap(inputs);
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
	
	public final synchronized Map<String, Input<? extends Serializable>> getInputs() {
		return this.inputs;
	}

	// FIXME: FunctionBlocks can still change the value with reflection. Get the value in doInit and keep it
	public final String getBlockType() {
		for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
			final Field field;
			try {
				field = c.getDeclaredField("BLOCK_TYPE");
			} catch (final SecurityException e) {
				continue;
			} catch (final NoSuchFieldException e) {
				continue;
			}
			final int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && String.class.equals(field.getType())) {
				field.setAccessible(true);
				try {
					return (String) field.get(null);
				} catch (IllegalArgumentException e) {
					continue;
				} catch (IllegalAccessException e) {
					continue;
				}
			}
		}
		return null;
	}

	// FIXME: FunctionBlocks can still change the value with reflection. Get the value in doInit and keep it
	public final long getUpdateInterval() {
		for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
			final Field field;
			try {
				field = c.getDeclaredField("BLOCK_UPDATE_INTERVAL");
			} catch (SecurityException e) {
				continue;
			} catch (NoSuchFieldException e) {
				continue;
			}
			final int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && long.class.equals(field.getType())) {
				field.setAccessible(true);
				Long value = null;
				try {
					value = (Long) field.get(null);
				} catch (IllegalArgumentException e) {
					continue;
				} catch (IllegalAccessException e) {
					continue;
				}
				if (value != null) {
					return value;
				}
			}
		}
		return Long.MIN_VALUE;
	}

	/**
	 * This method is called when a FunctionBlock is started on a module.
	 */
	public abstract void init();

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
		return "FunctionBlock[class=" + getClass() + ",blockUUID=" + getBlockUUID() + "]";
	}
}
