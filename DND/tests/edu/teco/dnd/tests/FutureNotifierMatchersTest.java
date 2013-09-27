package edu.teco.dnd.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.nullValue;

import static edu.teco.dnd.tests.FutureNotifierMatchers.*;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.FinishedFutureNotifier;
import edu.teco.dnd.util.FutureNotifier;

@RunWith(MockitoJUnitRunner.class)
public class FutureNotifierMatchersTest {
	@Mock
	private Object result;
	@Mock
	private Throwable cause;

	private FutureNotifier<Object> successfulFutureNotifier;
	private FutureNotifier<Object> failedFutureNotifier;
	private FutureNotifier<Object> unfinishedFutureNotifier;

	private FutureNotifier<Object> successfulNullFutureNotifier;
	private FutureNotifier<Object> failedNullFutureNotifier;

	@Before
	@SuppressWarnings("unchecked")
	public void setup() {
		successfulFutureNotifier = new FinishedFutureNotifier<Object>(result);
		failedFutureNotifier = new FinishedFutureNotifier<Object>(cause);
		unfinishedFutureNotifier = new DefaultFutureNotifier<Object>() {
		};

		successfulNullFutureNotifier = new FinishedFutureNotifier<Object>((Object) null);
		failedNullFutureNotifier = mock(FutureNotifier.class);
		when(failedNullFutureNotifier.cause()).thenReturn(null);
		when(failedNullFutureNotifier.isDone()).thenReturn(true);
		when(failedNullFutureNotifier.isSuccess()).thenReturn(false);
	}

	@Test
	public void testDoneSuccess() {
		assertThat(successfulFutureNotifier, done());
	}

	@Test
	public void testDoneFailure() {
		assertThat(failedFutureNotifier, done());
	}

	@Test
	public void testDoneUnfinished() {
		assertThat(unfinishedFutureNotifier, not(done()));
	}

	@Test
	public void testSuccessfulSuccess() {
		assertThat(successfulFutureNotifier, successful());
	}

	@Test
	public void testSuccessfulFailure() {
		assertThat(failedFutureNotifier, not(successful()));
	}

	@Test
	public void testSuccessfulUnfinished() {
		assertThat(unfinishedFutureNotifier, not(successful()));
	}

	@Test
	public void testFailedSuccess() {
		assertThat(successfulFutureNotifier, not(failed()));
	}

	@Test
	public void testFailedFailure() {
		assertThat(failedFutureNotifier, failed());
	}

	@Test
	public void testFailedUnfinished() {
		assertThat(unfinishedFutureNotifier, not(failed()));
	}

	@Test
	public void testHasResultSuccess() {
		assertThat(successfulFutureNotifier, hasResult(result));
	}

	@Test
	public void testHasResultNull() {
		assertThat(successfulNullFutureNotifier, hasResult(null));
	}

	@Test
	public void testHasResultNullWrong() {
		assertThat(successfulFutureNotifier, not(hasResult(null)));
	}

	@Test
	public void testHasResultWrong() {
		assertThat(successfulFutureNotifier, not(hasResult(mock(Object.class))));
	}

	@Test
	public void testHasResultFailure() {
		assertThat(failedFutureNotifier, not(hasResult(result)));
	}

	@Test
	public void testHasResultUnfinished() {
		assertThat(unfinishedFutureNotifier, not(hasResult(result)));
	}

	@Test
	public void testHasResultThatSuccess() {
		assertThat(successfulFutureNotifier, hasResultThat(is(notNullValue(Object.class))));
	}

	@Test
	public void testHasResultThatNull() {
		assertThat(successfulNullFutureNotifier, hasResultThat(is(nullValue())));
	}

	@Test
	public void testHasResultThatNullWrong() {
		assertThat(successfulFutureNotifier, hasResultThat(is(not(nullValue()))));
	}

	@Test
	public void testHasResultThatWrong() {
		assertThat(successfulFutureNotifier, not(hasResultThat(is(nullValue()))));
	}

	@Test
	public void testHasResultThatFailure() {
		assertThat(failedFutureNotifier, not(hasResultThat(any(Object.class))));
	}

	@Test
	public void testHasResultThatUnfinished() {
		assertThat(unfinishedFutureNotifier, not(hasResultThat(any(Object.class))));
	}

	@Test
	public void testHasCauseSuccess() {
		assertThat(successfulFutureNotifier, not(hasCause(cause)));
	}

	@Test
	public void testHasCauseFailure() {
		assertThat(failedFutureNotifier, hasCause(cause));
	}

	@Test
	public void testHasCauseNull() {
		assertThat(failedNullFutureNotifier, hasCause(null));
	}

	@Test
	public void testHasCauseNullWrong() {
		assertThat(failedFutureNotifier, not(hasCause(null)));
	}

	@Test
	public void testHasCauseWrong() {
		assertThat(failedFutureNotifier, not(hasCause(mock(Throwable.class))));
	}

	@Test
	public void testHasCauseUnfinshed() {
		assertThat(unfinishedFutureNotifier, not(hasCause(cause)));
	}

	@Test
	public void testHasCauseThatSuccess() {
		assertThat(successfulFutureNotifier, not(hasCauseThat(is(any(Throwable.class)))));
	}

	@Test
	public void testhasCauseThatThatFailure() {
		assertThat(failedFutureNotifier, hasCauseThat(is(any(Throwable.class))));
	}

	@Test
	public void testhasCauseThatThatNull() {
		assertThat(failedNullFutureNotifier, hasCauseThat(is(nullValue())));
	}

	@Test
	public void testhasCauseThatThatNullWrong() {
		assertThat(failedFutureNotifier, not(hasCauseThat(is(nullValue()))));
	}

	@Test
	public void testhasCauseThatThatWrong() {
		assertThat(failedFutureNotifier, not(hasCauseThat(is(nullValue()))));
	}

	@Test
	public void testhasCauseThatThatUnfinshed() {
		assertThat(unfinishedFutureNotifier, not(hasCauseThat(is(any(Throwable.class)))));
	}
}
