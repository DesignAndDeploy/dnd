package edu.teco.dnd.module.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class CombinedMatcher implements StackTraceElementMatcher {
	private final Collection<StackTraceElementMatcher> matchers = new ArrayList<StackTraceElementMatcher>();
	
	public CombinedMatcher(final StackTraceElementMatcher... matchers) {
		this.matchers.addAll(Arrays.asList(matchers));
	}
	
	public CombinedMatcher(final Collection<StackTraceElementMatcher> matchers) {
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
				return true;
			}
		}
		return false;
	}
}
