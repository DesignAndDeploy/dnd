package edu.teco.dnd.blocks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Class receiving information from an output. Opposed to {@link QueuedConnectionTarget} it does not store received
 * input and as thus only the current state of the output upon calling the methods can be received.
 */
public class SimpleConnectionTarget extends ConnectionTarget {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = -4833299172164509346L;

	/**
	 * The FunctionBlock this ConnectionTarget is connected to.
	 */
	private FunctionBlock functionBlock;

	/**
	 * The field this ConnectionTarget updates.
	 */
	private Field field;

	/**
	 * The value that was last received.
	 */
	private Serializable newValue = null;

	/**
	 * Whether or not there are changes not yet applied to the Field.
	 */
	private boolean dirty = false;

	/**
	 * Initializes a new SimpleConnectionTarget.
	 * 
	 * @param name
	 *            the name of the ConnectionTarget
	 * @param functionBlock
	 *            the functionBlock this SimpleConnectionTarget belongs to
	 * @param inputField
	 *            the inputField this SimpleConnectionTarget belongs to
	 */
	public SimpleConnectionTarget(final String name, final FunctionBlock functionBlock, final Field inputField) {
		super(name);
		if (functionBlock == null) {
			throw new IllegalArgumentException("functionBlock must not be null");
		}
		if (inputField == null) {
			throw new IllegalArgumentException("inputField must not be null");
		}
		if (!Serializable.class.isAssignableFrom(inputField.getType())) {
			throw new IllegalArgumentException("inputField must be a (subclass of) Serializable");
		}
		this.functionBlock = functionBlock;
		this.field = inputField;
	}

	@Override
	public final synchronized void setValue(final Serializable value) {
		this.newValue = value;
		this.dirty = true;
	}

	@Override
	public final synchronized boolean isDirty() {
		return this.dirty;
	}

	@Override
	public final void update() throws AssignmentException {
		synchronized (this.functionBlock) {
			synchronized (this) {
				if (this.dirty) {
					this.field.setAccessible(true);
					try {
						this.field.set(this.functionBlock, this.newValue);
					} catch (IllegalArgumentException e) {
						throw new AssignmentException("Failed to set input '" + field.getName() + "'", e);
					} catch (IllegalAccessException e) {
						throw new AssignmentException("Failed to set input '" + field.getName() + "'", e);
					}
					this.dirty = false;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Class<? extends Serializable> getType() {
		return (Class<? extends Serializable>) field.getType();
	}

	/**
	 * writes Objects to a Stream.
	 * 
	 * @param s
	 *            Stream to write to.
	 * @throws IOException
	 *             If streaming doesn't work properly.
	 */
	private void writeObject(final ObjectOutputStream s) throws IOException {
		s.writeObject(getName());
		s.writeObject(functionBlock);
		s.writeObject(newValue);
	}

	/**
	 * reads Objects from a Stream.
	 * 
	 * @param in
	 *            Stream to read from
	 * @throws IOException
	 *             If streaming doesn't work properly.
	 * @throws ClassNotFoundException
	 *             If class not found
	 */
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
		String name = (String) in.readObject();
		functionBlock = (FunctionBlock) in.readObject();
		newValue = (Serializable) in.readObject();
		field = null;
		for (Field f : FunctionBlock.getInputs(functionBlock.getClass())) {
			if (name.equalsIgnoreCase(f.getName())) {
				field = f;
			}
		}
	}
}
