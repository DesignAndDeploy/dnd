package edu.teco.dnd.module.permissions;

/**
 * A matcher for {@link StackTraceElement}s.
 */
public interface StackTraceElementMatcher {
	/**
	 * Checks if this matcher matches the given {@link StackTraceElement}.
	 * 
	 * @param stackTraceElement
	 *            the StackTraceElement to check
	 * @return true if this matcher matches the StackTraceElement
	 */
	boolean matches(StackTraceElement stackTraceElement);
}
