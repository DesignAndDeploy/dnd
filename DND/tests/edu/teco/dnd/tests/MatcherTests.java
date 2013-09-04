package edu.teco.dnd.tests;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ContainsInOrderTest.class, HasItemThatTest.class })
public class MatcherTests {
	static final List<Object> EMPTY_LIST = Collections.emptyList();
	static final List<Integer> INTEGER_LIST = Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4));
	static final List<String> STRING_LIST = Collections.unmodifiableList(Arrays.asList("foo", "bar", "foobar", "baz",
			"foobaz"));
	static final List<Integer> LIST_WITH_REPEATS = Collections.unmodifiableList(Arrays.asList(1, 2, 1, 3, 2));
}
