package edu.teco.dnd.module.permissions;

import java.security.Permission;

/**
 * A rule that decides whether or not a permission should be granted.
 * 
 * @author Philipp Adolf
 */
public interface PolicyRule {
	/**
	 * Decides whether or not the given Permission should be granted. The stackTrace will probably include method calls
	 * belonging to the security manager using this PolicyRule, so this should be considered when deciding what to do.
	 * 
	 * @param permission the permission that was requested
	 * @param stackTrace a current stack trace
	 * @return true to grant the permission, false to deny it or null if this PolicyRule cannot make a decision
	 */
	Boolean getPolicy(final Permission permission, final StackTraceElement[] stackTrace);
}
