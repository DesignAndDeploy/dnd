package edu.teco.dnd.tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ContainsInOrder extends TypeSafeMatcher<Iterable<?>> {
	private final List<? extends Object> expected;

	/**
	 * Initializes a new ContainsInOrder matcher with the given list of expected items. The list will not be copied, but it will not be modified by this matcher.
	 * @param expected the values the matcher will expect
	 */
	public ContainsInOrder(final List<? extends Object> expected) {
		this.expected = Collections.unmodifiableList(expected);
	}

	/**
	 * Initializes a new ContainsInOrder matcher with the given list of expected items. The list will be copied.
	 * 
	 * @param expected the values the matcher will expect
	 */
	public ContainsInOrder(final Object... expected) {
		this.expected = Collections.unmodifiableList(Arrays.asList(expected));
	}

	/**
	 * Returns a new ContainsInOrder matcher. Does not copy the list, but the list will not be modified by the new matcher.
	 * 
	 * @param expected the values the matcher will expect in the checked object(s)
	 * @return a new ContainsInOrder matcher
	 */
	public static ContainsInOrder containsInOrder(final List<? extends Object> expected) {
		return new ContainsInOrder(expected);
	}

	/**
	 * Returns a new ContainsInOrder matcher. Copies the list of expected items.
	 * 
	 * @param expected the values the matcher will expect in the checked object(s)
	 * @return a new ContainsInOrder matcher
	 */
	public static ContainsInOrder containsInOrder(final Object... expected) {
		return new ContainsInOrder(expected);
	}

	@Override
	public void describeTo(final Description description) {
		description.appendValueList("contains ", ",", " in order", expected);
	}

	@Override
	public boolean matchesSafely(final Iterable<?> actual) {
		final Iterator<?> iterator = actual.iterator();
		for (final Object expectedItem : expected) {
			if (!hasItem(expectedItem, iterator)) {
				return false;
			}
		}
		return true;
	}

	private boolean hasItem(final Object expectedItem, final Iterator<?> actual) {
		try {
			if (expectedItem == null) {
				findNull(actual);
			} else {
				findNonNull(expectedItem, actual);
			}
		} catch (final NoSuchElementException e) {
			return false;
		}
		return true;
	}

	private void findNull(final Iterator<?> actual) {
		Object item;
		do {
			item = actual.next();
		} while (item != null);
	}

	private void findNonNull(final Object expectedItem, final Iterator<?> actual) {
		Object item;
		do {
			item = actual.next();
		} while (!expectedItem.equals(item));
	}
}
