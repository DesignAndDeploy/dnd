package edu.teco.dnd.module;

/**
 * This class is used to wrap calls to certain standard functions. E.g. hashCode() or Equals() of objects that are not
 * trusted. Whenever this methods are used instead of the native calls, a certain amount of additional security is
 * applied.
 * 
 * @author Marvin Marx
 * 
 */
public final class UsercodeWrapper {
	/**
	 * utility class. Should never be instantiated.
	 */
	private UsercodeWrapper() {

	}

	/**
	 * returns obj.toString(). Should anything but a valid string be returned from this method (including, but not
	 * limited to thrown exceptions) this will be caught and transformed into a harmless UserSuppliedCodeException.
	 * 
	 * @param obj
	 *            the object to call toString on.
	 * @return obj.toString()
	 * @throws UserSuppliedCodeException
	 *             if anything goes wrong while executing toString()
	 */
	public static String getToString(Object obj) throws UserSuppliedCodeException {
		String toStr;
		try {
			toStr = obj.toString();
		} catch (Throwable t) {
			try {
				// Otherwise throwing a subclass of Exception, overriding getMessage() to
				// throw another exception (which has overridden functions) could leak code outside...
				throw new UserSuppliedCodeException(t.getMessage());
			} catch (Throwable t2) {
				throw new UserSuppliedCodeException();
			}
		}
		if (toStr == null) {
			throw new UserSuppliedCodeException("toString() returned null!");
		} else {
			return toStr;
		}
	}

	/**
	 * returns obj.hashCode(). Should anything but a valid int be returned from this method (read: thrown exceptions)
	 * this will be caught and transformed into a harmless UserSuppliedCodeException.
	 * 
	 * @param obj
	 *            the object to call hashCode on.
	 * @return obj.hashCode()
	 * @throws UserSuppliedCodeException
	 *             if anything goes wrong while executing hashCode()
	 */
	public static int getHashCode(Object obj) throws UserSuppliedCodeException {
		int hashCode;
		try {
			hashCode = obj.hashCode();
		} catch (Throwable t) {
			// Otherwise throwing a subclass of Exception, overriding getMessage() to
			// throw another exception (which has overridden functions) could leak code outside...
			try {
				throw new UserSuppliedCodeException(t.getMessage());
			} catch (Throwable t2) {
				throw new UserSuppliedCodeException();
			}
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
	 * @throws IllegalArgumentException
	 *             if one == null;
	 */
	public static boolean getEquals(Object one, Object two) throws UserSuppliedCodeException, IllegalArgumentException {
		if (one == null) {
			throw new IllegalArgumentException("argument one must not be null");
		}
		boolean equal;
		try {
			equal = one.equals(two);
		} catch (Throwable t) {
			// Otherwise throwing a subclass of Exception, overriding getMessage() to
			// throw another exception (which has overridden functions) could leak code outside...
			try {
				throw new UserSuppliedCodeException(t.getMessage());
			} catch (Throwable t2) {
				throw new UserSuppliedCodeException();
			}
		}
		return equal;
	}
}