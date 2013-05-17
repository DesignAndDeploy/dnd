package edu.teco.dnd.blocks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class receiving information from an output. Opposed to {@link SimpleConnectionTarget} it stores received
 * input and as thus the information is not lost, if not immediatly requested.
 */
public class QueuedConnectionTarget extends ConnectionTarget {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3181191213370664363L;

	/**
	 * The FunctionBlock this ConnectionTarget is connected to.
	 */
	private FunctionBlock functionBlock;

	/**
	 * The field this ConnectionTarget updates.
	 */
	private Field field;

	/**
	 * Used to store values coming from the connected {@link Output}.
	 */
	private Queue<Serializable> values = new LinkedList<Serializable>();

	/**
	 * Initializes a QueuedConnectionTarget.
	 * 
	 * @param name
	 *            the name of the ConnectionTarget
	 * @param functionBlock
	 *            the {@link FunctionBlock} this QueuedConnectionTarget belongs to
	 * @param inputField
	 *            the {@link Field} that is the actual input
	 */
	public QueuedConnectionTarget(final String name, final FunctionBlock functionBlock, final Field inputField) {
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

	/**
	 * Adds the value to the Queue.
	 * 
	 * @param value
	 *            the new value of the connected Output
	 */
	@Override
	public final synchronized void setValue(final Serializable value) {
		values.add(value);
	}

	/**
	 * Returns true if there are still values that have not been processed.
	 * 
	 * @return true if there are still values that have not been processed
	 */
	@Override
	public final synchronized boolean isDirty() {
		return !values.isEmpty();
	}

	@Override
	public final void update() throws AssignmentException {
		synchronized (this.functionBlock) {
			synchronized (this) {
				if (values.isEmpty()) {
					return;
				}
				Serializable value = values.remove();
				field.setAccessible(true);
				try {
					field.set(this.functionBlock, value);
				} catch (IllegalArgumentException e) {
					throw new AssignmentException("Failed to set input '" + field.getName() + "'", e);
				} catch (IllegalAccessException e) {
					throw new AssignmentException("Failed to set input '" + field.getName() + "'", e);
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
		s.writeObject(values);
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
	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
		String name = (String) in.readObject();
		functionBlock = (FunctionBlock) in.readObject();
		values = (Queue<Serializable>) in.readObject();
		field = null;
		for (Field f : FunctionBlock.getInputs(functionBlock.getClass())) {
			if (name.equalsIgnoreCase(f.getName())) {
				field = f;
			}
		}
	}
}
