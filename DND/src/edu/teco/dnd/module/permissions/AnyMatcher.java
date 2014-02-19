package edu.teco.dnd.module.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Matches if any of the given {@link StackTraceElementMatcher}s matches. This class is not thread-safe.
 */
public class AnyMatcher implements StackTraceElementMatcher {
	private static final Logger LOGGER = LogManager.getLogger(AnyMatcher.class);

	private final Collection<StackTraceElementMatcher> matchers = new ArrayList<StackTraceElementMatcher>();

	public AnyMatcher(final StackTraceElementMatcher... matchers) {
		this.matchers.addAll(Arrays.asList(matchers));
	}

	public AnyMatcher(final Collection<StackTraceElementMatcher> matchers) {
		this.matchers.addAll(matchers);
	}

	public void add(final StackTraceElementMatcher matcher) {
		matchers.add(matcher);
	}

	public void addAll(final Collection<StackTraceElementMatcher> matchers) {
		this.matchers.addAll(matchers);
	}

	public void remove(final StackTraceElementMatcher matcher) {
		matchers.remove(matcher);
	}

	public void removeAll(final Collection<StackTraceElementMatcher> matchers) {
		this.matchers.removeAll(matchers);
	}

	@Override
	public boolean matches(final StackTraceElement stackTraceElement) {
		for (final StackTraceElementMatcher matcher : matchers) {
			if (matcher.matches(stackTraceElement)) {
				LOGGER.trace("{} matched {}", matcher, stackTraceElement);
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("AnyMatcher[");
		boolean first = true;
		for (final StackTraceElementMatcher matcher : matchers) {
			if (first) {
				first = false;
			} else {
				sb.append(',');
			}
			sb.append(matcher);
		}
		sb.append("]");
		return sb.toString();
	}
}
