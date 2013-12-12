package edu.teco.dnd.module;

import java.io.ObjectStreamClass;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.messages.values.ValueMessageAdapter;

public class ApplicationSecurityManager extends SecurityManager {
	private static final Logger LOGGER = LogManager.getLogger(ApplicationSecurityManager.class);

	private static final StackTraceElementMatcher INSECURE;
	static {
		final CombinedMatcher insecureMatcher = new CombinedMatcher();
		insecureMatcher.add(new ClassMatcher(UsercodeWrapper.class));
		insecureMatcher.add(new ClassMatcher(FunctionBlockSecurityDecorator.class));
		insecureMatcher.add(new MethodMatcher(ObjectStreamClass.class, "invokeReadObject"));
		insecureMatcher.add(new MethodMatcher(ObjectStreamClass.class, "invokeReadResolve"));
		INSECURE = insecureMatcher;
	}

	private static final StackTraceElementMatcher SECURE;
	static {
		final CombinedMatcher secureMatcher = new CombinedMatcher();
		secureMatcher.add(new MethodMatcher(FunctionBlock.class, "doInit"));
		secureMatcher.add(new MethodMatcher(Application.class, "sendValue"));
		secureMatcher.add(new MethodMatcher(ClassLoader.class, "loadClass"));
		secureMatcher.add(new ClassMatcher(ValueMessageAdapter.class));
		SECURE = secureMatcher;
	}

	private static final StackTraceElementMatcher JIT_SECURE = new MethodMatcher(FunctionBlockSecurityDecorator.class, "<init>");
	private static final StackTraceElementMatcher APPLICATION_SECURITY_MANAGER_MATCHER = new ClassMatcher(ApplicationSecurityManager.class);

	private static final PermissionCollection SECURE_PERMISSIONS;
	static {
		SECURE_PERMISSIONS = new Permissions();
		SECURE_PERMISSIONS.add(new RuntimePermission("getClassLoader"));
		SECURE_PERMISSIONS.add(new RuntimePermission("getenv.*"));
		SECURE_PERMISSIONS.add(new RuntimePermission("getFileSystemAttributes"));
		SECURE_PERMISSIONS.add(new RuntimePermission("getFileSystemAttributes"));
		SECURE_PERMISSIONS.setReadOnly();
	}

	private boolean isInsideSecureMethod(final StackTraceElement[] stackTrace) {
		LOGGER.entry((Object) stackTrace);
		if (stackTrace == null || stackTrace.length == 0) {
			LOGGER.warn("stack trace is empty");
		}
		for (final StackTraceElement stackTraceElement : stackTrace) {
			if (INSECURE.matches(stackTraceElement)) {
				LOGGER.exit(false);
				return false;
			} else if (SECURE.matches(stackTraceElement)) {
				LOGGER.exit(true);
				return true;
			}
		}
		LOGGER.exit(true);
		return true;
	}

	@Override
	public void checkPermission(final Permission permission) {
		LOGGER.entry(permission);
		if (SECURE_PERMISSIONS.implies(permission)) {
			LOGGER.exit();
			return;
		}

		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		if (isInsideSecureMethod(stackTrace)) {
			LOGGER.exit();
			return;
		}

		if (isJITCall(permission, stackTrace)) {
			LOGGER.exit();
			return;
		}

		LOGGER.warn("not allowing {}", permission);
		throw new SecurityException();
	}

	private boolean isJITCall(final Permission permission, final StackTraceElement[] stackTrace) {
		if ((!(permission instanceof RuntimePermission) || !"createClassLoader".equals(permission.getName())) &&
				(!(permission instanceof ReflectPermission) || !"suppressAccessChecks".equals(permission.getName()))) {
			return false;
		}
		for (final StackTraceElement stackTraceElement : stackTrace) {
			if (JIT_SECURE.matches(stackTraceElement)) {
				return true;
			} else if (!stackTraceElement.getClassName().startsWith("java.")
					&& !stackTraceElement.getClassName().startsWith("sun.")
					&& !APPLICATION_SECURITY_MANAGER_MATCHER.matches(stackTraceElement)) {
				return false;
			}
		}
		return false;
	}

	@Override
	public void checkPermission(final Permission perm, final Object context) {
		checkPermission(perm);
	}
}
