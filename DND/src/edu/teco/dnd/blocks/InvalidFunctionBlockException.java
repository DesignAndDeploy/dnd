package edu.teco.dnd.blocks;

/**
 * This exception is thrown if a FunctionBlock is not well defined.
 * 
 * @author philipp
 */
public class InvalidFunctionBlockException extends Exception {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = -7115194255279811936L;

	/**
	 * Initializes a new InvalidFunctionBlockException.
	 * 
	 * @param msg
	 *            an error message
	 * @param cause
	 *            what caused this exception to be thrown.
	 */
	public InvalidFunctionBlockException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Initializes a new InvalidFunctionBlockException.
	 * 
	 * @param cause
	 *            what caused this exception to be thrown.
	 */
	public InvalidFunctionBlockException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Initializes a new InvalidFunctionBlockException.
	 * 
	 * @param msg
	 *            an error message
	 */

	public InvalidFunctionBlockException(final String msg) {
		super(msg);
	}

	/**
	 * Initializes a new InvalidFunctionBlockException.
	 */
	public InvalidFunctionBlockException() {
		super();
	}
}
