package edu.teco.dnd.module.permissions;

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
}
