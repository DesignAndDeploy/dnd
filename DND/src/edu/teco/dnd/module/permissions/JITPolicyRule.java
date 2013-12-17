package edu.teco.dnd.module.permissions;

import java.lang.reflect.ReflectPermission;
import java.security.Permission;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.FunctionBlockSecurityDecorator;

/**
 * A PolicyRule to make JIT work. See Bug #51: When calling a constructor of a class for the 16th time, the Oracle and
 * IcedTea VM switch from interpreted code to JIT'ed native code. For this they create a new ClassLoader, which a
 * FunctionBlock normally is not allowed to do. This PolicyRule grants this permission and "suppressAcccessChecks" if
 * it determines that the JIT is currently working.
 * 
 * May need some improvement, at the moment it assumes that java.*, sun.* and edu.teco.dnd.module.permissions.* are
 * safe. Also, a FunctionBlock constructor can probably use both permissions even if it's for something else than the
 * JIT.
 * 
 * @author Philipp Adolf
 */
public class JITPolicyRule implements PolicyRule {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final StackTraceElementMatcher SECURE = new MethodMatcher(FunctionBlockSecurityDecorator.class,
			"<init>");

	@Override
	public Boolean getPolicy(final Permission permission, final StackTraceElement[] stackTrace) {
		LOGGER.entry(permission, stackTrace);
		if (!isJITPermission(permission)) {
			return LOGGER.exit(null);
		}
		for (final StackTraceElement stackTraceElement : stackTrace) {
			if (SECURE.matches(stackTraceElement)) {
				return LOGGER.exit(true);
			} else if (!isJITMethod(stackTraceElement)) {
				return LOGGER.exit(null);
			}
		}
		return LOGGER.exit(null);
	}

	private boolean isJITMethod(final StackTraceElement stackTraceElement) {
		return stackTraceElement.getClassName().startsWith("java.")
				|| stackTraceElement.getClassName().startsWith("sun.")
				|| stackTraceElement.getClassName().startsWith("edu.teco.dnd.module.permissions.");
	}

	private boolean isJITPermission(final Permission permission) {
		if (permission instanceof RuntimePermission && "createClassLoader".equals(permission.getName())) {
			return true;
		}
		if (permission instanceof ReflectPermission && "suppressAccessChecks".equals(permission.getName())) {
			return true;
		}
		return false;
	}
}
