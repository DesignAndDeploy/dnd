package edu.teco.dnd.util;

import org.apache.bcel.classfile.JavaClass;

/**
 * Filters classes. Used by {@link ClassScanner}.
 */
public interface ClassFilter {
	/**
	 * Whether or not the class is accepted by the filter.
	 * 
	 * @param cls
	 *            the class to check
	 * @return <code>true</code> if the class should be accepted
	 */
	boolean acceptClass(JavaClass cls);
}
