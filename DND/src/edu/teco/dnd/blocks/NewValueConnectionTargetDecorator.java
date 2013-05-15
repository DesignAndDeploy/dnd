package edu.teco.dnd.blocks;

import java.io.Serializable;

/**
 * A decorator for ConnectionTarget that discards new values if they are equal to the last value that was set.
 * null is discarded if the last value set was null, for non null values equals is called.
 * 
 * @author philipp
 */
public class NewValueConnectionTargetDecorator extends ConnectionTarget implements Serializable {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = -7988009178659077150L;

	/**
	 * The decorated ConnectionTarget.
	 */
	private final ConnectionTarget realConnectionTarget;

	/**
	 * If this is the first time a value is set.
	 */
	private boolean firstValue = true;

	/**
	 * The last value that was set.
	 */
	private Serializable lastValue = null;

	/**
	 * Initializes a new ConnectionTarget decorator.
	 * 
	 * @param realConnectionTarget
	 *            the ConnectionTarget to decorate. Must not be null.
	 */
	public NewValueConnectionTargetDecorator(final ConnectionTarget realConnectionTarget) {
		super(realConnectionTarget.getName());
		this.realConnectionTarget = realConnectionTarget;
	}

	@Override
	public Class<? extends Serializable> getType() {
		return realConnectionTarget.getType();
	}

	@Override
	public synchronized void setValue(final Serializable value) {
		if (!firstValue) {
			if (lastValue == null) {
				if (value == null) {
					return;
				}
			} else if (lastValue.equals(value)) {
				return;
			}
		} else {
			firstValue = false;
		}
		lastValue = value;
		realConnectionTarget.setValue(value);
	}

	/**
	 * Checks if the realConnectionTarget is dirty.
	 * 
	 * @return true if the real connection target is dirty
	 */
	@Override
	public boolean isDirty() {
		return realConnectionTarget.isDirty();
	}

	@Override
	public void update() throws AssignmentException {
		realConnectionTarget.update();
	}

	/**
	 * Creates a new decorated Connection Target.
	 * 
	 * @return decorated ConnectionTarget
	 */
	public final ConnectionTarget getDecoratedConnectionTarget() {
		return realConnectionTarget;
	}
}
