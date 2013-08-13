package edu.teco.dnd.util;

/**
 * Provides utility methods for handling Strings.
 * 
 * @author Philipp Adolf
 */
public class StringUtil {
	/**
	 * Joins an array of objects into a String. The objects will be separated by the given separator String. If null is
	 * passed null is returned.
	 * 
	 * @param array
	 *            the objects to join. Method will return null if null is passed for this parameter
	 * @param separator
	 *            String to put between each object. Will
	 *            <em>not<em> be added before the first or after the last object. If null nothing will be
	 * 		put between the objects.
	 * @return a concatenation of all objects separated by <code>separator</code> or null if null was passed for
	 *         <code>array</code>
	 */
	public static <T> String joinArray(T[] array, String separator) {
		if (array == null) {
			return null;
		}

		if (separator == null) {
			separator = "";
		}
		final StringBuilder sb = new StringBuilder();
		boolean addSeparator = false;
		for (final T object : array) {
			if (addSeparator) {
				sb.append(separator);
			} else {
				addSeparator = true;
			}
			sb.append(object);
		}

		return sb.toString();
	}
}
