package edu.teco.dnd.blocks;

import java.io.Serializable;

/**
 * A wrapper for a field annotated with {@link Input}.
 */
public abstract class ConnectionTarget implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5869722130037723660L;

	/**
	 * The name of this ConnectionTarget.
	 */
	private final String name;

	/**
	 * The Output that this functionBlock is connected to.
	 */
	private Output<?> connectedOutput = null;

	/**
	 * Initializes a ConnectionTarget.
	 * 
	 * @param name
	 *            the name of the ConnectionTarget
	 */
	public ConnectionTarget(final String name) {
		this.name = name;
	}

	/**
	 * Sets the {@link Output} this ConnectionTarget is connected to. Will call
	 * {@link Output#removeConnection(ConnectionTarget)} on the old connected Output.
	 * 
	 * @param connectedOutput
	 *            the Output to connect to. Must be of a compatible type.
	 */
	public final void setConnectedOutput(final Output<?> connectedOutput) {
		if (this.connectedOutput == connectedOutput) {
			return;
		}
		if (this.connectedOutput != null) {
			this.connectedOutput.removeConnection(this);
		}
		this.connectedOutput = connectedOutput;
		if (this.connectedOutput != null) {
			this.connectedOutput.addConnection(this);
		}
	}

	/**
	 * Returns the Output connected to this ConnectionTarget. Returns null if no Output is connected.
	 * 
	 * @return the Output connected to this ConnectionTarget or null if there is no such Output
	 */
	public final Output<?> getConnectedOutput() {
		return connectedOutput;
	};

	/**
	 * Returns the type of the input.
	 * 
	 * @return the type of the input
	 */
	public abstract Class<? extends Serializable> getType();

	/**
	 * Returns the name of the ConnectionTarget.
	 * 
	 * @return the name of the ConnectionTarget
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Tells the ConnectionTarget that the value of the connected Output has changed. This should only be
	 * called by the connected Output.
	 * 
	 * @param value
	 *            the new value of the connected Output
	 */
	public abstract void setValue(Serializable value);

	/**
	 * Returns true if there are still values that have not been processed.
	 * 
	 * @return true if there are still values that have not been processed
	 */
	public abstract boolean isDirty();

	/**
	 * Updates the value of the Input.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	public abstract void update() throws AssignmentException;

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectedOutput == null) ? 0 : connectedOutput.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ConnectionTarget other = (ConnectionTarget) obj;
		if (connectedOutput == null) {
			if (other.connectedOutput != null) {
				return false;
			}
		} else if (!connectedOutput.equals(other.connectedOutput)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
