package edu.teco.dnd.module.permissions;

/**
 * A matcher for StackTraceElements.
 * 
 * @author Philipp Adolf
 */
public interface StackTraceElementMatcher {
	/**
	 * Checks if this matcher matches the given StackTraceElement.
	 * 
	 * @param stackTraceElement the StackTraceElement to check
	 * @return true if this matcher matches the StackTraceElement
	 */
	boolean matches(StackTraceElement stackTraceElement);
}
