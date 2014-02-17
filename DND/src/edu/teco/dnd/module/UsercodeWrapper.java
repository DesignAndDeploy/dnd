package edu.teco.dnd.module;

/**
 * Provides utility methods that wrap {@link Object#toString()}, {@link Object#hashCode()} and
 * {@link Object#equals(Object)} and catch any {@link Throwable} thrown.
 */
public final class UsercodeWrapper {
	/**
	 * This class should only be used for its static methods, so the constructor is private.
	 */
	private UsercodeWrapper() {

	}

	/**
	 * Wrapper for {@link Object#toString()} that will catch any {@link Throwable} thrown.
	 * 
	 * @param object
	 *            the object to call <code>toString()</code> on
	 * @return whatever <code>object.toString()</code> returns
	 * @throws UserSuppliedCodeException
	 *             if any {@link Throwable} was thrown
	 */
	public static String getToString(Object object) throws UserSuppliedCodeException {
		try {
			return object.toString();
		} catch (Throwable t) {
			// ignoring the Throwable as it could be used to make code being executed without a wrapper like this method
			throw new UserSuppliedCodeException();
		}
	}

	/**
	 * Wrapper for {@link Object#hashCode()} that will catch any {@link Throwable} thrown.
	 * 
	 * @param object
	 *            the object to call <code>hashCode()</code> on
	 * @return whatever <code>object.hashCode()</code> returns
	 * @throws UserSuppliedCodeException
	 *             if any {@link Throwable} was thrown
	 */
	public static int getHashCode(Object object) throws UserSuppliedCodeException {
		try {
			return object.hashCode();
		} catch (Throwable t) {
			// ignoring the Throwable as it could be used to make code being executed without a wrapper like this method
			throw new UserSuppliedCodeException();
		}
	}

	/**
	 * Wrapper for {@link Object#equals(Object)} that will catch any {@link Throwable} thrown.
	 * 
	 * @param object
	 *            the object to call <code>equals(Object)</code> on
	 * @param other
	 *            the parameter for <code>equals(Object)</code> on
	 * @return the result of <code>object.equals(other)
	 * @throws UserSuppliedCodeException
	 *             if any Throwable is thrown
	 */
	public static boolean getEquals(Object object, Object other) throws UserSuppliedCodeException {
		try {
			return object.equals(other);
		} catch (Throwable t) {
			// ignoring the Throwable as it could be used to make code being executed without a wrapper like this method
			throw new UserSuppliedCodeException();
		}
	}
}