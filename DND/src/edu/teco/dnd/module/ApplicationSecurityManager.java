package edu.teco.dnd.module;

import java.awt.AWTPermission;
import java.io.FilePermission;
import java.io.SerializablePermission;
import java.lang.management.ManagementPermission;
import java.lang.reflect.ReflectPermission;
import java.net.NetPermission;
import java.net.SocketPermission;
import java.nio.file.LinkPermission;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.SecurityPermission;
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

	public ApplicationSecurityManager() {
		super();
	}

	/**
	 * check if we are being called from within a functionBlocks doUpdate() or init() methode.
	 * 
	 * @return true if we are inside an application, and not within other context.
	 */
	private boolean isApplication() {
		Thread currentThread = Thread.currentThread();
		if (currentThread == null) {
			throw new SecurityException();
			// Thread group is dead, which is strange in this context.
		}

		for (StackTraceElement ste : currentThread.getStackTrace()) {
			if (ste.getClassName().contains("edu.teco.dnd.module.BlockRunner")) {
				// We are inside a FunctionBlocks doUpdate() or init()
				// == the stack contains BlockRunner somewhere, which marks that we are in user code.
				return true;
			}
		}
		return false;
	}

	@Override
	public void checkPermission(Permission perm) {
		LOGGER.entry(perm);
		if (!isApplication()) {
			LOGGER.exit("permission granted.");
			return;
		}
		LOGGER.trace("permission {} requested by app.", perm);

		if (perm instanceof SocketPermission) {
		} else if (perm instanceof FilePermission) {
		} else if (perm instanceof BasicPermission) {
			BasicPermission bPerm = (BasicPermission) perm;
			if (bPerm instanceof AudioPermission) {
				LOGGER.exit("permission granted.");
				return; // Allowed
			} else if (bPerm instanceof WebServicePermission) {
				LOGGER.exit("permission granted.");
				return; // Allowed
			} else if (bPerm instanceof AWTPermission) {
				if (bPerm.getName().equals("accessSystemTray")) {
					return; // Allow
				} else if (bPerm.getName().equals("fullScreenExclusive")) {
					return; // Allow
				} else if (bPerm.getName().equals("fullScreenExclusive")) {
					return; // Allow
				} else if (bPerm.getName().equals("setWindowAlwaysOnTop")) {
					return; // Allow
				} else if (bPerm.getName().equals("watchMousePointer")) {
					return; // Allow
				}
				//Deny
			} else if (bPerm instanceof NetPermission) {
				if (bPerm.getName().equals("getNetworkInformation")) {
					return; // Allow
				} else if (bPerm.getName().equals("getProxySelector")) {
					return; // Allow
				}
				//Deny
			} else if (bPerm instanceof RuntimePermission) {
				if (bPerm.getName().equals("getClassLoader")) {
					return; // Allow
				} else if (bPerm.getName().equals("getProtectionDomain")) {
					return; // Allow
				}
				//Deny
				
				//TODO rework with boolean doAllow = false;
			} else if (bPerm instanceof AuthPermission) {
			} else if (bPerm instanceof LinkPermission) {
			} else if (bPerm instanceof LoggingPermission) {
			} else if (bPerm instanceof ManagementPermission) {
			} else if (bPerm instanceof PropertyPermission) {
			} else if (bPerm instanceof ReflectPermission) {
			} else if (bPerm instanceof SecurityPermission) {
			} else if (bPerm instanceof SerializablePermission) {
			}
		}

		//Default deny.
		LOGGER.warn("Permission: {}, denied for application.", perm);
		LOGGER.exit("permission denied.");
		throw new SecurityException();

	}

	@Override
	public void checkPermission(Permission perm, Object context) {
		checkPermission(perm);
	}

}
