package edu.teco.dnd.module.permissions;

import java.lang.reflect.Method;

/**
 * A StackTraceElementMatcher that matches a method by its name and the name of the class.
 */
public class MethodMatcher implements StackTraceElementMatcher {
	private final String className;
	private final String methodName;

	public MethodMatcher(final String className, final String methodName) {
		this.className = className;
		this.methodName = methodName;
	}

	public MethodMatcher(final Class<?> cls, final String methodName) {
		this(cls.getName(), methodName);
	}

	public MethodMatcher(final Method method) {
		this(method.getDeclaringClass().getName(), method.getName());
	}

	@Override
	public boolean matches(final StackTraceElement stackTraceElement) {
		return className.equals(stackTraceElement.getClassName())
				&& methodName.equals(stackTraceElement.getMethodName());
	}

	@Override
	public String toString() {
		return "MethodMatcher[" + className + "." + methodName + "]";
	}
}
