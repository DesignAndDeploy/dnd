package edu.teco.dnd.module;

import java.awt.AWTPermission;
import java.io.FilePermission;
import java.io.SerializablePermission;
import java.lang.management.ManagementPermission;
import java.lang.reflect.ReflectPermission;
import java.net.NetPermission;
import java.net.SocketPermission;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.SecurityPermission;
import java.util.Collection;
import java.util.LinkedList;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import javax.security.auth.AuthPermission;
import javax.sound.sampled.AudioPermission;
import javax.xml.ws.WebServicePermission;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * the security manager designed to limit assess rights of functionBlocks.
 * 
 * @author Marvin Marx
 * 
 */
public class ApplicationSecurityManager extends SecurityManager {

	private static final Logger LOGGER = LogManager.getLogger(ApplicationSecurityManager.class);

	/**
	 * Classes/Methods after passing through which code is considered insecure. Class/Method names can be partial
	 * (missing beginning and/or end). specifying an array with only 1 element, means the whole class. Syntax: <br>
	 * String[0]=classname ; <br>
	 * String[1] = method name.(optional)
	 */
	private static final Collection<String[]> INSECURE_METHODS = new LinkedList<String[]>();

	/**
	 * Methods the code can call after which execution is considered privileged again.<br>
	 * Syntax: <br>
	 * String[0]=fully qualified classname ; <br>
	 * String[1] = method name.
	 * 
	 */
	private static final Collection<String[]> SECURED_METHODS = new LinkedList<String[]>();

	// FIXME: I'm not exactly sure which class Base64$1 is or what it is needed for
	private static final Class<?> BASE64_CLASS;
	/**
	 * How far above the current code the given method in Base64 class is, if we are decoding content we received in a
	 * message and having to use accessDeclaredMembers. Hard to avoid, as we really need to make sure that nothing else
	 * can use that privilege.<br>
	 * It is a bit nasty!
	 */
	private static final int NESTING_LEVEL_FOR_BASE64_CLASS = 15;
	static {
		Class<?> cls = null;
		try {
			cls = Class.forName("edu.teco.dnd.util.Base64$1");
		} catch (final ClassNotFoundException e) {
			LOGGER.catching(e);
		}
		BASE64_CLASS = cls;
	}

	static {
		INSECURE_METHODS.add(new String[] { "edu.teco.dnd.module.UsercodeWrapper" });
		INSECURE_METHODS.add(new String[] { "edu.teco.dnd.module.FunctionBlockSecurityDecorator" });
		INSECURE_METHODS.add(new String[] { "java.io.ObjectStreamClass", "invokeReadObject" });
		INSECURE_METHODS.add(new String[] { "java.io.ObjectStreamClass", "invokeReadResolve" });

		SECURED_METHODS.add(new String[] { "edu.teco.dnd.module.FunctionBlockSecurityDecorator", "doInit" });
		SECURED_METHODS.add(new String[] { "edu.teco.dnd.module.Application", "sendValue" });
		// sending need special privileges.
		SECURED_METHODS.add(new String[] { "java.lang.ClassLoader", "loadClass" });
		// Default class loader needs FilePrivileges (not sure whether safe).
		SECURED_METHODS.add(new String[] { "edu.teco.dnd.blocks.FunctionBlock", "getInputs" });
		SECURED_METHODS.add(new String[] { "edu.teco.dnd.blocks.FunctionBlock", "getOptions" });
		SECURED_METHODS.add(new String[] { "edu.teco.dnd.blocks.FunctionBlock", "getTimebetweenSchedules" });

		SECURED_METHODS.add(new String[] { "edu.teco.dnd.util.Base64$1", "<init>" });
		SECURED_METHODS.add(new String[] { "java.io.ObjectInputStream", "readObject" });
	}

	/** Constructor. */
	public ApplicationSecurityManager() {
		super();
	}

	/**
	 * check if we are being called from within privileged code (meaning not a functionBlocks init) or doUpdate(). if a
	 * known secured function is called after that permissions are still granted.)
	 * 
	 * @return true if we are inside an application, and not within other context.
	 */
	private boolean isPrivilegedCode() {
		String[] reasonForGrant = null;
		boolean isPrivileged = true;
		Thread currentThread = Thread.currentThread();
		if (currentThread == null) {
			throw new SecurityException();
			// Thread group is dead, which is strange in this context.
		}

		StackTraceElement[] stackTrace = currentThread.getStackTrace();

		for (int i = stackTrace.length - 1; i >= 0; i--) {
			StackTraceElement ste = stackTrace[i];
			for (String[] str : INSECURE_METHODS) {
				if (ste.getClassName().contains(str[0])) {
					if (str.length == 1 || ste.getMethodName().contains(str[1])) {
						// stack shows we are inside code that is considered unsafe.
						isPrivileged = false;
						break;
					}
				}
			}
			for (String[] str : SECURED_METHODS) {
				if (ste.getClassName().equals(str[0]) && ste.getMethodName().equals(str[1])) {
					// Code assumed safe again;
					if (!isPrivileged) {
						isPrivileged = true;
						reasonForGrant = str;
					}
					break;
					// FIXME: we really need a finer distinction than this.
				}
			}
		} // Next ste;
		if (isPrivileged && reasonForGrant != null) {
			LOGGER.trace("granting privileges because of {}.{}", reasonForGrant[0], reasonForGrant[1]);
		}
		return isPrivileged;
	}

	@Override
	public void checkPermission(Permission perm) {
		LOGGER.entry(perm);
		if (isPrivilegedCode()) {
			LOGGER.trace("Allowing privileged code to do {}.", perm);
			return;
		}
		LOGGER.trace("permission {} requested by app.", perm);
		if (perm.getName() == null) {
			LOGGER.warn("Illegal permission request with empty getName()");
			Thread.dumpStack();
			throw new SecurityException("Illegal permission request with empty getName()");
		}
		boolean doAllow = false;
		if (perm instanceof SocketPermission) {
			// Deny
		} else if (perm instanceof FilePermission) {
			// Deny
		} else if (perm instanceof BasicPermission) {
			BasicPermission bPerm = (BasicPermission) perm;
			if (bPerm instanceof AudioPermission) {
				doAllow = true;
			} else if (bPerm instanceof WebServicePermission) {
				doAllow = true;
			} else if (bPerm instanceof AWTPermission) {
				if ("accessSystemTray".equals(bPerm.getName())) {
					doAllow = true;
				} else if ("fullScreenExclusive".equals(bPerm.getName())) {
					doAllow = true;
				} else if ("fullScreenExclusive".equals(bPerm.getName())) {
					doAllow = true;
				} else if ("setWindowAlwaysOnTop".equals(bPerm.getName())) {
					doAllow = true;
				} else if ("watchMousePointer".equals(bPerm.getName())) {
					doAllow = true;
				} else {
					// Deny
				}
			} else if (bPerm instanceof NetPermission) {
				if ("getNetworkInformation".equals(bPerm.getName())) {
					doAllow = true;
				} else if ("getProxySelector".equals(bPerm.getName())) {
					doAllow = true;
				} else {
					// Deny
				}
			} else if (bPerm instanceof RuntimePermission) {
				if ("getClassLoader".equals(bPerm.getName())) {
					doAllow = true;
				} else if ("getProtectionDomain".equals(bPerm.getName())) {
					doAllow = true;
				} else if ("accessDeclaredMembers".equals(bPerm.getName())) {
					StackTraceElement trace = Thread.currentThread().getStackTrace()[NESTING_LEVEL_FOR_BASE64_CLASS];
					if (BASE64_CLASS.equals(trace.getClass()) && "readObject".equals(trace.getMethodName())) {
						doAllow = true;
					}
				} else {
					// Deny
				}
			} else if (bPerm instanceof AuthPermission) {
			} else if (bPerm instanceof LoggingPermission) {
			} else if (bPerm instanceof ManagementPermission) {
			} else if (bPerm instanceof PropertyPermission) {
			} else if (bPerm instanceof ReflectPermission) {
			} else if (bPerm instanceof SecurityPermission) {
			} else if (bPerm instanceof SerializablePermission) {
			}
		}

		if (doAllow) {
			LOGGER.trace("Allowed permission {}.", perm);
			LOGGER.exit("permission granted.");
			return;
		} else {
			LOGGER.warn("Permission: {}, denied for application.", perm);
			LOGGER.exit("permission denied.");
			throw new SecurityException();
		}
	}

	@Override
	public void checkPermission(Permission perm, Object context) {
		checkPermission(perm);
	}

}
