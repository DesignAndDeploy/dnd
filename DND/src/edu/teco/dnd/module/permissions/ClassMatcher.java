package edu.teco.dnd.module.permissions;

/**
 * Matches if the class name of a StackTraceElement is the same as the one given in the constructor. The method name
 * of the StackTraceElement is ignored.
 * 
 * @author Philipp Adolf
 */
public class ClassMatcher implements StackTraceElementMatcher {
	private final String className;
	
	public ClassMatcher(final String className) {
		this.className = className;
	}
	
	public ClassMatcher(final Class<?> cls) {
		this(cls.getName());
	}

	@Override
	public boolean matches(final StackTraceElement stackTraceElement) {
		return className.equals(stackTraceElement.getClassName());
	}
	
	@Override
	public String toString() {
		return "ClassMatcher[" + className + "]";
	}
}
