package edu.teco.dnd.module.permissions;

import java.security.Permission;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A PolicyRule based on {@link StackTraceElementMatcher}s. This rule has two lists of StackTraceElementMatchers: one
 * for secure StackTraceElements and one for insecure ones. This PolicyRule iterates over the stack trace and if either
 * one of the StackTraceElementMatchers matches, the permission is granted (if the secure list matches) or denied (if
 * the insecure list matches). If both match the permission is granted. If no matcher matches any StackTraceElement,
 * then no decision is made.
 */
public class StackTraceElementMatcherPolicyRule implements PolicyRule {
	private static final Logger LOGGER = LogManager.getLogger(StackTraceElementMatcherPolicyRule.class);

	private final AnyMatcher secureMatcher = new AnyMatcher();

	private final AnyMatcher insecureMatcher = new AnyMatcher();

	public void addSecureMatcher(final StackTraceElementMatcher matcher) {
		secureMatcher.add(matcher);
	}

	public void addAllSecureMatchers(final Collection<StackTraceElementMatcher> matchers) {
		secureMatcher.addAll(matchers);
	}

	public void addInsecureMatcher(final StackTraceElementMatcher matcher) {
		insecureMatcher.add(matcher);
	}

	public void addAllInsecureMatchers(final Collection<StackTraceElementMatcher> matchers) {
		insecureMatcher.addAll(matchers);
	}

	@Override
	public Boolean getPolicy(final Permission permission, final StackTraceElement[] stackTrace) {
		LOGGER.entry(permission, stackTrace);
		for (final StackTraceElement stackTraceElement : stackTrace) {
			if (secureMatcher.matches(stackTraceElement)) {
				LOGGER.trace("got positive match for {}", stackTraceElement);
				return LOGGER.exit(true);
			} else if (insecureMatcher.matches(stackTraceElement)) {
				LOGGER.trace("got negative match for {}", stackTraceElement);
				return LOGGER.exit(false);
			}
		}
		return LOGGER.exit(null);
	}
}
