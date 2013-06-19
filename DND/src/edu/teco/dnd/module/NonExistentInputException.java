package edu.teco.dnd.module;

/**
 * Thrown if an accessed input does not exist on the FunctionBlock it was accessed on.
 * 
 */
public class NonExistentInputException extends Exception {

	private static final long serialVersionUID = -2255346140627213512L;

	/**
	 * Initializes a new NonExistentInputException.
	 * 
	 * @param msg
	 *            an error message
	 * @param cause
	 *            what caused this exception to be thrown.
	 */
	public NonExistentInputException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Initializes a new NonExistentInputException.
	 * 
	 * @param msg
	 *            an error message
	 */
	public NonExistentInputException(final String msg) {
		super(msg);
	}

	/**
	 * Initializes a new NonExistentInputException.
	 * 
	 * @param cause
	 *            what caused this exception to be thrown.
	 */
	public NonExistentInputException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Initializes a new NonExistentInputException.
	 */
	public NonExistentInputException() {
		super();
	}
}
