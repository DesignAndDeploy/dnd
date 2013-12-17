package edu.teco.dnd.module.permissions;

import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A PolicyRun that automatically grants a given set of Permissions. The stack trace is ignored.
 * 
 * @author Philipp Adolf
 */
public class GrantPermissionPolicyRule implements PolicyRule {
	public static final Logger LOGGER = LogManager.getLogger(GrantPermissionPolicyRule.class);
	
	private final PermissionCollection permissions = new Permissions();

	public void addPermission(final Permission permission) {
		permissions.add(permission);
	}

	@Override
	public Boolean getPolicy(final Permission permission, final StackTraceElement[] stackTrace) {
		LOGGER.entry(permission, stackTrace);
		if (permissions.implies(permission)) {
			return LOGGER.exit(true);
		}
		return LOGGER.exit(null);
	}
}
