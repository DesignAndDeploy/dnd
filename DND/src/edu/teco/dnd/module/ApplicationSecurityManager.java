package edu.teco.dnd.module;

import java.security.Permission;

/**
 * the security manager designed to limit assess rights of functionBlocks.
 * 
 * @author Marvin Marx
 * 
 */
public class ApplicationSecurityManager extends SecurityManager {

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
		if (!isApplication()) {
			return;
		}
		
		throw new SecurityException();
		// TODO: implement security here.

	}
}
