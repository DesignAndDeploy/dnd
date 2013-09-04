package edu.teco.dnd.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static edu.teco.dnd.tests.HasItemThat.hasItemThat;
import static edu.teco.dnd.tests.HasItemThat.hasNoItemThat;

import org.junit.Test;

public class HasItemThatTest {
	private final Collection<Object> EMPTY_COLLECTION = Collections.emptyList();
	private final Collection<Integer> INTEGER_COLLECTION = Collections
			.unmodifiableCollection(Arrays.asList(1, 2, 3, 4));
	private final Collection<String> STRING_COLLECTION = Collections.unmodifiableCollection(Arrays.asList("foo", "bar",
			"foobar", "baz"));

	@Test
	public void testItemExists() {
		assertThat(INTEGER_COLLECTION, hasItemThat(is(equalTo(1))));
		assertThat(INTEGER_COLLECTION, hasItemThat(is(equalTo(2))));
		assertThat(INTEGER_COLLECTION, hasItemThat(is(equalTo(3))));
		assertThat(STRING_COLLECTION, hasItemThat(is(equalTo("foo"))));
		assertThat(STRING_COLLECTION, hasItemThat(is(equalTo("bar"))));
		assertThat(STRING_COLLECTION, hasItemThat(is(equalTo("foobar"))));
		assertThat(STRING_COLLECTION, hasItemThat(is(equalTo("baz"))));
	}

	@Test
	public void testItemDoesNotExist() {
		assertThat(EMPTY_COLLECTION, hasNoItemThat(is(not(anything()))));
		assertThat(INTEGER_COLLECTION, hasNoItemThat(is(not(anything()))));
		assertThat(STRING_COLLECTION, hasNoItemThat(is(not(anything()))));
	}

	@Test
	public void testMultipleItemsMatching() {
		assertThat(INTEGER_COLLECTION, hasItemThat(is(anything())));
		assertThat(STRING_COLLECTION, hasItemThat(is(anything())));
	}
}
