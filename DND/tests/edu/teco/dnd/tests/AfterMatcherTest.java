package edu.teco.dnd.tests;

import static edu.teco.dnd.tests.MatcherTests.EMPTY_LIST;
import static edu.teco.dnd.tests.MatcherTests.INTEGER_LIST;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static edu.teco.dnd.tests.AfterMatcher.after;

import java.util.Collections;

import org.junit.Test;

public class AfterMatcherTest {
	@Test
	public void testCorrectOrder() {
		assertThat(2, is(after(1).in(INTEGER_LIST)));
		assertThat(3, is(after(1).in(INTEGER_LIST)));
		assertThat(4, is(after(1).in(INTEGER_LIST)));
		assertThat(3, is(after(2).in(INTEGER_LIST)));
		assertThat(4, is(after(2).in(INTEGER_LIST)));
		assertThat(4, is(after(3).in(INTEGER_LIST)));
	}
	
	@Test
	public void testEmptyList() {
		assertThat(1, is(not(after(0).in(EMPTY_LIST))));
	}
	
	@Test
	public void testSingleElementList() {
		assertThat(1, is(not(after(0).in(Collections.singleton(1)))));
	}
	
	@Test
	public void testWrongOrder() {
		assertThat(1, is(not(after(2).in(INTEGER_LIST))));
		assertThat(1, is(not(after(3).in(INTEGER_LIST))));
		assertThat(1, is(not(after(4).in(INTEGER_LIST))));
		assertThat(2, is(not(after(3).in(INTEGER_LIST))));
		assertThat(2, is(not(after(4).in(INTEGER_LIST))));
		assertThat(3, is(not(after(4).in(INTEGER_LIST))));
	}
}
