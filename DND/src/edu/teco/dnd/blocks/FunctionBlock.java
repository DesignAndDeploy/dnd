package edu.teco.dnd.blocks;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Base class function blocks. Subclasses have to implement {@link #init()} and {@link #update()}.
 * 
 * @see Input
 * @see Output
 * @see Option
 * @see Timed
 */
public abstract class FunctionBlock implements Serializable {
	/**
	 * ID used for serialization.
	 */
	private static final long serialVersionUID = -4973200709811091483L;

	/**
	 * The ID of this FunctionBlock.
	 */
	private final String id;

	/**
	 * Stores all {@link ConnectionTarget}s used by this function block. Mapped from their name.
	 */
	private final Map<String, ConnectionTarget> connectionTargets = new HashMap<String, ConnectionTarget>();

	/**
	 * Stores all {@link Output}s used by this function block. Mapped from their name. Lazily initialized by
	 * {@link #getOutputs()} as the variables will not have been initialized when the constructor is called.
	 */
	private Map<String, Output<?>> outputs = null;

	/**
	 * Stores all {@link Option}s used by this function block.
	 */
	private final Set<String> options = new HashSet<String>();

	/**
	 * The position this block wants to be at.
	 */
	private String position = null;

	/**
	 * Returns all inputs defined in the given class. If a subclass has an input with the same name as a superclass the
	 * input of the subclass takes precedence.
	 * 
	 * @param cls
	 *            the class to inspect. Must not be null.
	 * @return all inputs defined in the class
	 */
	public static Set<Field> getInputs(final Class<? extends FunctionBlock> cls) {
		if (cls == null) {
			throw new IllegalArgumentException("cls must not be null");
		}
		Set<Field> inputs = new HashSet<Field>();
		Set<String> names = new HashSet<String>();
		for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
			for (Field field : c.getDeclaredFields()) {
				Input inputAnnotation = field.getAnnotation(Input.class);
				if (inputAnnotation != null && !names.contains(field.getName())) {
					inputs.add(field);
					names.add(field.getName());
				}
			}
		}
		return inputs;
	}

	/**
	 * Returns a set containing all fields that are Outputs. If a subclass has an output with the same name as a
	 * superclass the output of the subclass takes precedence.
	 * 
	 * @param cls
	 *            the class to inspect. Must not be null.
	 * @return all outputs defined in the class
	 */
	public static Set<Field> getOutputs(final Class<? extends FunctionBlock> cls) {
		if (cls == null) {
			throw new IllegalArgumentException("cls must not be null");
		}
		Set<Field> outputs = new HashSet<Field>();
		Set<String> names = new HashSet<String>();
		for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
			for (Field field : c.getDeclaredFields()) {
				if (Output.class.isAssignableFrom(field.getType()) && !names.contains(field.getName())) {
					outputs.add(field);
					names.add(field.getName());
				}
			}
		}
		return outputs;
	}

	/**
	 * Returns a set containing all fields that are marked as Options. If a subclass has an option with the same name as
	 * a superclass the option of the subclass takes precedence.
	 * 
	 * @param cls
	 *            the class to inspect. Must not be null.
	 * @return all options defined in the class
	 */
	public static Set<Field> getOptions(final Class<? extends FunctionBlock> cls) {
		if (cls == null) {
			throw new IllegalArgumentException("cls must not be null");
		}
		Set<Field> options = new HashSet<Field>();
		Set<String> names = new HashSet<String>();
		for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
			for (Field field : c.getDeclaredFields()) {
				Option optionAnnotation = field.getAnnotation(Option.class);
				if (optionAnnotation != null && !names.contains(field.getName())) {
					options.add(field);
					names.add(field.getName());
				}
			}
		}
		return options;
	}

	/**
	 * Queries all {@link Input}s and {@link Option}s for the function block and creates a timer as specified by a
	 * {@link Timed} annotation (if present).
	 * 
	 * @param id
	 *            the ID of this FunctionBlock
	 */
	public FunctionBlock(final String id) {
		this.id = id;

		for (Field input : getInputs(getClass())) {
			String name = input.getName();
			ConnectionTarget ct = null;
			Input inputAnnotation = input.getAnnotation(Input.class);
			if (inputAnnotation.value()) {
				ct = new QueuedConnectionTarget(name, this, input);
			} else {
				ct = new SimpleConnectionTarget(name, this, input);
			}
			if (inputAnnotation.newOnly()) {
				ct = new NewValueConnectionTargetDecorator(ct);
			}
			this.connectionTargets.put(name, ct);
		}

		for (Field option : getOptions(getClass())) {
			options.add(option.getName());
		}
	}

	/**
	 * returns the timeinterval this block wishes to be scheduled at.
	 * 
	 * @return the timeinterval this block wishes to be scheduled at, a value less than 0 if no timer is desired.
	 */
	public long getTimebetweenSchedules() {
		Timed timed = getClass().getAnnotation(Timed.class);
		if (timed != null && timed.value() > 0) {
			return timed.value();
		} else {
			return -1;
		}
	}

	/**
	 * Returns the type identifier for this block.
	 * 
	 * @return the type identifier for this block
	 */
	public abstract String getType();

	/**
	 * Can be used to initialize data used by the function block. All {@link Option}s will have been set and will not
	 * change afterwards. Will be called before {@link #update()} is called.
	 */
	public abstract void init();

	/**
	 * Returns all {@link ConnectionTarget}s used by this function block. The key is the name of the input, the value is
	 * the matching ConnnectionTarget.
	 * 
	 * @return a Map from input names to ConnectionTargets
	 */
	public final Map<String, ConnectionTarget> getConnectionTargets() {
		return connectionTargets;
	}

	/**
	 * Returns all {@link Output}s used by this function block. The key is the name of the output, the value is the
	 * matching ConnectionTarget.
	 * 
	 * @return a Map from output names to Outputs.
	 * @throws InvalidFunctionBlockException
	 *             if this FunctionBlock has ill defined Outputs
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final synchronized Map<String, Output<?>> getOutputs() throws InvalidFunctionBlockException {
		if (outputs == null) {
			outputs = new HashMap<String, Output<?>>();
			for (Field output : getOutputs(getClass())) {
				output.setAccessible(true);
				Output<?> out = null;
				try {
					out = (Output<?>) output.get(this);
				} catch (IllegalArgumentException e) {
					throw new InvalidFunctionBlockException("Cannot get Output '" + output.getName() + "'", e);
				} catch (IllegalAccessException e) {
					throw new InvalidFunctionBlockException("Cannot get Output '" + output.getName() + "'", e);
				}
				if (out == null) {
					out = new Output(output.getName());
					try {
						output.set(this, out);
					} catch (IllegalArgumentException e) {
						throw new InvalidFunctionBlockException("Cannot set Output '" + output.getName() + "'", e);
					} catch (IllegalAccessException e) {
						throw new InvalidFunctionBlockException("Cannot set Output '" + output.getName() + "'", e);
					}
				}
				if (output.getGenericType() instanceof ParameterizedType
						&& ((ParameterizedType) output.getGenericType()).getActualTypeArguments().length == 1) {
					out.setType((Class) ((ParameterizedType) output.getGenericType()).getActualTypeArguments()[0]);
				}
				outputs.put(output.getName(), out);
			}
		}
		return outputs;
	}

	/**
	 * Returns all {@link Option}s used by this function block. The key is the name of the option, the value is the
	 * type.
	 * 
	 * @return all Options used by this function block
	 */
	public final Map<String, Type> getOptions() {
		Map<String, Type> opts = new HashMap<String, Type>();
		for (String name : options) {
			Field field = null;
			for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
				try {
					field = c.getDeclaredField(name);
					opts.put(name, field.getType());
					break;
				} catch (NoSuchFieldException e) {
				} catch (SecurityException e) {
				}
			}
		}
		return opts;
	}

	/**
	 * Sets the option with the given name to the given value.
	 * 
	 * @param name
	 *            the name of the option to set. Must be in the Map returned by {@link #getOptions()}.
	 * @param value
	 *            the value to set the option to. Must be assignable to the type of the option.
	 * @throws AssignmentException
	 *             if setting the option fails
	 */
	public final void setOption(final String name, final Serializable value) throws AssignmentException {
		if (!options.contains(name)) {
			throw new IllegalArgumentException("Invalid name");
		}
		Field field = null;
		for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
			try {
				field = c.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
			} catch (SecurityException e) {
			}
		}
		if (field == null) {
			throw new AssignmentException("unknown field");
		}
		if (value != null && !field.getType().isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException("INvalid type");
		}
		field.setAccessible(true);
		try {
			field.set(this, value);
		} catch (IllegalArgumentException e) {
			throw new AssignmentException("Cannot assign option '" + name + "'", e);
		} catch (IllegalAccessException e) {
			throw new AssignmentException("Cannot assign option '" + name + "'", e);
		}
	}

	/**
	 * Retrieves the value of an Option.
	 * 
	 * @param name
	 *            the name of the option. Must be one of the names returned by {@link #getOptions()}.
	 * @return the value of the option
	 * @throws RetrievementException
	 *             if retrieving the value failed
	 */
	public final Serializable getOption(final String name) throws RetrievementException {
		if (!options.contains(name)) {
			throw new IllegalArgumentException("Invalid name");
		}
		Field field = null;
		for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
			try {
				field = c.getDeclaredField(name);
				break;
			} catch (NoSuchFieldException e) {
			} catch (SecurityException e) {
			}
		}
		if (field == null) {
			throw new RetrievementException("unknown field");
		}
		Serializable result = null;
		field.setAccessible(true);
		try {
			result = (Serializable) field.get(this);
		} catch (IllegalArgumentException e) {
			throw new RetrievementException("Cannot get option '" + name + "'", e);
		} catch (IllegalAccessException e) {
			throw new RetrievementException("Cannot get option '" + name + "'", e);
		}
		return result;
	}

	/**
	 * Returns the ID of this FunctionBlock.
	 * 
	 * @return the ID of this FunctionBlock
	 */
	public final String getID() {
		return id;
	}

	/**
	 * Returns whether or not the block needs an update. This will also reset the timer if it is the reason for the
	 * update. The default implementation returns true if the timer wants to trigger an update or if any
	 * ConnectionTarget is dirty.
	 * 
	 * @return true if the block needs an update, false otherwise
	 */
	public final synchronized boolean isDirty() {
		boolean dirty = false;
		for (ConnectionTarget connectionTarget : connectionTargets.values()) {
			if (connectionTarget.isDirty()) {
				dirty = true;
				break;
			}
		}
		return dirty;
	}

	/**
	 * Used by subclasses to implement the actual logic. Called by {@link #doUpdate()}.
	 */
	protected abstract void update();

	/**
	 * Updates the block. This includes calling {@link ConnectionTarget#update()} on all ConnectionTargets that say that
	 * they are dirty and calling {@link #update()} afterwards.
	 * 
	 * @throws AssignmentException
	 *             if assigning a variable fails
	 */
	public final synchronized void doUpdate() throws AssignmentException {
		for (ConnectionTarget connectionTarget : connectionTargets.values()) {
			synchronized (connectionTarget) {
				if (connectionTarget.isDirty()) {
					connectionTarget.update();
				}
			}
		}
		update();
	}

	/**
	 * Returns the position this block wants to be at. This is a Java regex specifier.
	 * 
	 * @return the position this block wants to be at
	 */
	public final String getPosition() {
		return this.position;
	}

	/**
	 * Sets the position this block wants to be at. This is a Java regex specifier.
	 * 
	 * @param position
	 *            the position
	 */
	public final void setPosition(final String position) {
		this.position = position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectionTargets == null) ? 0 : connectionTargets.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result + ((outputs == null) ? 0 : outputs.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	// Auto-generated by eclipse.
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionBlock other = (FunctionBlock) obj;
		if (connectionTargets == null) {
			if (other.connectionTargets != null)
				return false;
		} else if (!connectionTargets.equals(other.connectionTargets))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (options == null) {
			if (other.options != null)
				return false;
		} else if (!options.equals(other.options))
			return false;
		if (outputs == null) {
			if (other.outputs != null)
				return false;
		} else if (!outputs.equals(other.outputs))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}
}
