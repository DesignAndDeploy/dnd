package edu.teco.dnd.module;

/**
 * This class is used to wrap calls to certain standard functions. E.g. ashCode() or Equals() of objects that are not
 * trusted. Whenever this methods are used instead of the native calls, a certain amount of additional security is
 * applied.
 * 
 * @author Marvin Marx
 * 
 */
public class UsercodeWrapper {

	public static String getToString(Object obj) throws UserSuppliedCodeException {
		String toStr;
		try {
			toStr = obj.toString();
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
		if (toStr == null) {
			throw new UserSuppliedCodeException("Blocktype must not be null!");
		} else {
			return toStr;
		}
	}

	public static int getHashCode(Object obj) throws UserSuppliedCodeException {
		int hashCode;
		try {
			hashCode = obj.hashCode();
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
		return hashCode;
	}

	/**
	 * security wrapper for the equals method. Wraps the call which would usually be one.equals(two);
	 * 
	 * @param one
	 *            first argument
	 * @param two
	 *            second argument to equals
	 * @return the result of one.equals(two);
	 * @throws UserSuppliedCodeException
	 *             if there was an unexpected exception.
	 * @throws NullPointerException
	 *             if one ==null;
	 */
	public static boolean getEquals(Object one, Object two) throws UserSuppliedCodeException {
		if (one == null) {
			throw new IllegalArgumentException("one must not be null");
		}
		boolean equal;
		try {
			equal = one.equals(two);
		} catch (Throwable t) {
			throw new UserSuppliedCodeException(t);
		}
		return equal;
	}
}