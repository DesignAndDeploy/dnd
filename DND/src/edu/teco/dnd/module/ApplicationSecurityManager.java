package edu.teco.dnd.module;

import java.security.Permission;

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
			// TODO: force this to go through a proper wrapper for easier discernibility.
			// Application$3 (anonymous subclass) is nasty
			// TODO: handle doUpdate() of blocks as well.
			if (ste.getClassName().contains("edu.teco.dnd.module.Application$")) {
				// We are inside a FunctionBlocks doUpdate() or init()
				// (== the runnable that was scheduled in Application.java, which is part of the stack.
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
//		throw new SecurityException();
		// TODO: implement security here.

	}
}
