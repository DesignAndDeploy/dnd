package edu.teco.dnd.module;

import java.io.IOException;

import edu.teco.dnd.util.Base64;

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
			try {
				// Otherwise throwing a subclass of Exception, overriding getMessage() to
				// throw another exception (which has overridden functions) could leak code outside...
				throw new UserSuppliedCodeException(t.getMessage());
			} catch (Throwable t2) {
				throw new UserSuppliedCodeException();
			}
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

	public static Object base64DecodeToObject(String encodedObject, int options, final ClassLoader loader)
			throws ClassNotFoundException, IOException, UserSuppliedCodeException {
		Object obj;

		// It's not beautiful, but otherwise throwing a subclass of CNFexception, overriding getMessage() to
		// throw another exception (which has overridden functions) could leak rough code outside...
		try {
			obj = Base64.decodeToObject(encodedObject, options, loader);
		} catch (ClassNotFoundException e) {
			try {
				throw new ClassNotFoundException(e.getMessage()); // Sanitizing
			} catch (Throwable t) {
				throw new UserSuppliedCodeException();
			}
		} catch (IOException e) {
			try {
				throw new IOException(e.getMessage());
			} catch (Throwable t) {
				throw new UserSuppliedCodeException();
			}
		} catch (Throwable t) {
			try {
				throw new UserSuppliedCodeException(t.getMessage());
			} catch (Throwable t2) {
				throw new UserSuppliedCodeException();
			}
		}
		return obj;
	}
}