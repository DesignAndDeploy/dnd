package edu.teco.dnd.util;

/**
 * Provides utility methods for handling {@link String}s.
 */
public final class StringUtil {
	/**
	 * Private constructor as this class should not be instantiated.
	 */
	private StringUtil() {
	}

	/**
	 * Joins an array of objects into a String. The objects will be separated by the given separator String.
	 * 
	 * @param array
	 *            the objects to join. Method will return <code>null</code> if <code>null</code> is passed for this
	 *            parameter
	 * @param separator
	 *            String to put between each object. Will <em>not</em> be added before the first or after the last
	 *            object. If <code>null</code> nothing will be put between the objects.
	 * @return a concatenation of all objects separated by <code>separator</code> or <code>null</code> if
	 *         <code>null</code> was passed for <code>array</code>
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

	/**
	 * Joins the elements of an iterable into a String. The objects will be separated by the given separator String.
	 * 
	 * @param iterable
	 *            the objects to join. Method will return <code>null</code> if <code>null</code> is passed for this
	 *            parameter
	 * @param separator
	 *            String to put between each object. Will <em>not</em> be added before the first or after the last
	 *            object. If null nothing will be put between the objects.
	 * @return a concatenation of all objects separated by <code>separator</code> or <code>null</code> if
	 *         <code>null</code> was passed for <code>iterable</code>
	 */
	public static <T> String joinIterable(Iterable<T> iterable, String separator) {
		if (iterable == null) {
			return null;
		}

		if (separator == null) {
			separator = "";
		}
		final StringBuilder sb = new StringBuilder();
		boolean addSeparator = false;
		for (final T object : iterable) {
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
