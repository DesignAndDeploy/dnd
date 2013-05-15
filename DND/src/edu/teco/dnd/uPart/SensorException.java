package edu.teco.dnd.uPart;

/**
 * Occurs if something went wrong with a sensor.
 */
public class SensorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 948594695531115185L;

	/**
	 * Initializes a new SensorException.
	 * 
	 * @param msg
	 *            message of exception
	 * @param t
	 *            why exception was triggered
	 */
	public SensorException(final String msg, final Throwable t) {
		super(msg, t);
	}
}
