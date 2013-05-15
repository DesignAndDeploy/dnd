package edu.teco.dnd.blocks;

/**
 * This exception is thrown if assigning a field of a FunctionBlock fails.
 * 
 * @author philipp
 * 
 */
public class AssignmentException extends Exception {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = -8932555249496488555L;

	/**
	 * Initializes a new AssignmentException.
	 * 
	 * @param msg
	 *            an error message
	 * @param cause
	 *            what caused this exception to be thrown.
	 */
	public AssignmentException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Initializes a new AssignmentException.
	 * 
	 * @param msg
	 *            an error message
	 */
	public AssignmentException(final String msg) {
		super(msg);
	}

	/**
	 * Initializes a new AssignmentException.
	 * 
	 * @param cause
	 *            what caused this exception to be thrown.
	 */
	public AssignmentException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Initializes a new AssignmentException.
	 */
	public AssignmentException() {
		super();
	}
}
