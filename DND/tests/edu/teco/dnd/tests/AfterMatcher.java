package edu.teco.dnd.tests;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class AfterMatcher extends TypeSafeMatcher<Object> {
	private final Object otherItem;
	private final Iterable<?> iterable;

	public AfterMatcher(final Object otherItem, final Iterable<?> iterable) {
		this.otherItem = otherItem;
		this.iterable = iterable;
	}
	
	public static Factory after(final Object otherItem) {
		return new Factory(otherItem);
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("after ").appendValue(otherItem).appendText(" in ").appendValue(iterable);
	}

	@Override
	public boolean matchesSafely(final Object item) {
		boolean foundOtherItem = false;
		for (final Object currentItem : iterable) {
			if (equals(item, currentItem)) {
				return foundOtherItem;
			} else if (equals(otherItem, currentItem)) {
				foundOtherItem = true;
			}
		}
		return false;
	}

	private boolean equals(final Object a, final Object b) {
		if (a == null) {
			return b == null;
		}
		return a.equals(b);
	}
	
	public static class Factory {
		private final Object otherItem;
		
		private Factory(final Object otherItem) {
			this.otherItem = otherItem;
		}
		
		public AfterMatcher in(final Iterable<?> iterable) {
			return new AfterMatcher(otherItem, iterable);
		}
	}
}
