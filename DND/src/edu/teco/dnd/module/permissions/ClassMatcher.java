package edu.teco.dnd.module.permissions;

/**
 * Matches if the class name of the {@link StackTraceElement} is the same as the one given in the constructor. The
 * method name of the StackTraceElement is ignored.
 */
public class ClassMatcher implements StackTraceElementMatcher {
	private final String className;

	/**
	 * Initializes a new ClassMatcher that will match any {@link StackTraceElement} with the class name
	 * <code>className</code>.
	 * 
	 * @param className
	 *            the class name to match
	 */
	public ClassMatcher(final String className) {
		this.className = className;
	}

	/**
	 * Initializes a new ClassMatcher that will match any {@link StackTraceElement} with a class name that is equal to
	 * the name of <code>cls</code>.
	 * 
	 * @param cls
	 *            the Class to match
	 */
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
