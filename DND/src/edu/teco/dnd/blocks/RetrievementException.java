package edu.teco.dnd.blocks;

/**
 * This is thrown if receiving a field of a FunctionBlock failed.
 * 
 * @author philipp
 */
public class RetrievementException extends Exception {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = -4223233250259946717L;

	/**
	 * Initializes a new RetrievementException.
	 * 
	 * @param msg
	 *            the error message
	 * @param cause
	 *            the throwable that caused the exception
	 */
	public RetrievementException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Initializes a new RetrievementException.
	 * 
	 * @param cause
	 *            the throwable that caused the exception
	 */
	public RetrievementException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Initializes a new RetrievementException.
	 * 
	 * @param msg
	 *            the error message
	 */
	public RetrievementException(final String msg) {
		super(msg);
	}

	/**
	 * Initializes a new RetrievementException.
	 */
	public RetrievementException() {
		super();
	}
}
