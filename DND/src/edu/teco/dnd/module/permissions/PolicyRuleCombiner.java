package edu.teco.dnd.module.permissions;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

/**
 * Combines multiple PolicyRules, returning the first result that is not null.
 * 
 * @author Philipp Adolf
 */
public class PolicyRuleCombiner implements PolicyRule {
	private final List<PolicyRule> rules = new ArrayList<PolicyRule>();

	public void addRule(final PolicyRule rule) {
		rules.add(rule);
	}

	@Override
	public Boolean getPolicy(final Permission permission, final StackTraceElement[] stackTrace) {
		for (final PolicyRule rule : rules) {
			final Boolean policy = rule.getPolicy(permission, stackTrace);
			if (policy != null) {
				return policy;
			}
		}
		return null;
	}
}
