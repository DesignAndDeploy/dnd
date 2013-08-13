package edu.teco.dnd.module;

/**
 * thrown if an accessed FunctionBlock does not exist on the Module it was accessed on.
 * 
 */
public class NonExistentFunctionblockException extends Exception {

	private static final long serialVersionUID = -2255346140627213512L;

	/**
	 * Initializes a new NonExistentFunctionblockException.
	 * 
	 * @param msg
	 *            an error message
	 * @param cause
	 *            what caused this exception to be thrown.
	 */
	public NonExistentFunctionblockException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Initializes a new NonExistentFunctionblockException.
	 * 
	 * @param msg
	 *            an error message
	 */
	public NonExistentFunctionblockException(final String msg) {
		super(msg);
	}

	/**
	 * Initializes a new NonExistentFunctionblockException.
	 * 
	 * @param cause
	 *            what caused this exception to be thrown.
	 */
	public NonExistentFunctionblockException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Initializes a new NonExistentFunctionblockException.
	 */
	public NonExistentFunctionblockException() {
		super();
	}
}
