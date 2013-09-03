package edu.teco.dnd.tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThat;
import static edu.teco.dnd.tests.ContainsInOrder.containsInOrder;
import static org.hamcrest.CoreMatchers.not;

import org.junit.Test;

public class ContainsInOrderTest {
	private static final List<Object> EMPTY_LIST = Collections.emptyList();
	private static final List<Integer> INTEGER_LIST = Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4));
	private static final List<String> STRING_LIST = Collections.unmodifiableList(Arrays.asList("foo", "bar", "foobar",
			"baz", "foobaz"));
	private static final List<Integer> LIST_WITH_REPEATS = Collections.unmodifiableList(Arrays.asList(1, 2, 1, 3, 2));

	@Test
	public void testEmptyExpected() {
		assertThat(EMPTY_LIST, containsInOrder());
		assertThat(INTEGER_LIST, containsInOrder());
		assertThat(STRING_LIST, containsInOrder());
	}

	@Test
	public void testSingleExpected() {
		assertThat(INTEGER_LIST, containsInOrder(3));
		assertThat(STRING_LIST, containsInOrder("bar"));
	}

	@Test
	public void testTwoExpected() {
		assertThat(INTEGER_LIST, containsInOrder(1, 2));
		assertThat(INTEGER_LIST, containsInOrder(2, 4));
		assertThat(STRING_LIST, containsInOrder("foo", "foobaz"));
		assertThat(STRING_LIST, containsInOrder("bar", "foobar"));
	}

	@Test
	public void testMultipleExpected() {
		assertThat(INTEGER_LIST, containsInOrder(1, 2, 3));
		assertThat(INTEGER_LIST, containsInOrder(1, 2, 4));
		assertThat(INTEGER_LIST, containsInOrder(1, 3, 4));
		assertThat(INTEGER_LIST, containsInOrder(2, 3, 4));
		assertThat(STRING_LIST, containsInOrder("foo", "bar", "foobar"));
		assertThat(STRING_LIST, containsInOrder("foo", "bar", "foobar", "baz"));
		assertThat(STRING_LIST, containsInOrder("foo", "bar", "foobar", "foobaz"));
		assertThat(STRING_LIST, containsInOrder("foo", "foobar", "baz", "foobaz"));
		assertThat(STRING_LIST, containsInOrder("bar", "foobar", "baz", "foobaz"));
	}

	@Test
	public void testAllExpected() {
		assertThat(INTEGER_LIST, containsInOrder(INTEGER_LIST));
		assertThat(STRING_LIST, containsInOrder(STRING_LIST));
	}

	@Test
	public void testValueExpectedTwice() {
		assertThat(LIST_WITH_REPEATS, containsInOrder(1, 1));
		assertThat(LIST_WITH_REPEATS, containsInOrder(2, 2));
		assertThat(LIST_WITH_REPEATS, containsInOrder(1, 2, 1, 2));
		assertThat(LIST_WITH_REPEATS, containsInOrder(1, 1, 3));
		assertThat(LIST_WITH_REPEATS, containsInOrder(2, 3, 2));
	}

	@Test
	public void testMissingValue() {
		assertThat(EMPTY_LIST, not(containsInOrder(new Object())));
		assertThat(INTEGER_LIST, not(containsInOrder(42)));
		assertThat(STRING_LIST, not(containsInOrder("barfoo")));
	}

	@Test
	public void testWrongType() {
		assertThat(INTEGER_LIST, not(containsInOrder("foobar")));
		assertThat(STRING_LIST, not(containsInOrder(42)));
	}

	@Test
	public void testSecondMissing() {
		assertThat(INTEGER_LIST, not(containsInOrder(1, 1)));
		assertThat(STRING_LIST, not(containsInOrder("foo", "foo")));
	}

	@Test
	public void testThirdMissing() {
		assertThat(LIST_WITH_REPEATS, not(containsInOrder(1, 1, 1)));
	}

	@Test
	public void testWrongOrder() {
		assertThat(INTEGER_LIST, not(containsInOrder(2, 1)));
		assertThat(INTEGER_LIST, not(containsInOrder(1, 2, 4, 3)));
		assertThat(STRING_LIST, not(containsInOrder("bar", "foo")));
		assertThat(STRING_LIST, not(containsInOrder("foo", "baz", "bar")));
	}
}
