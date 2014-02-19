package edu.teco.dnd.network.tcp.tests;

import static edu.teco.dnd.tests.FutureNotifierMatchers.done;
import static edu.teco.dnd.tests.FutureNotifierMatchers.hasCause;
import static edu.teco.dnd.tests.FutureNotifierMatchers.hasResult;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.network.tcp.ResponseFutureManager;
import edu.teco.dnd.network.tcp.ResponseFutureManager.ResponseFutureNotifier;
import edu.teco.dnd.util.UUIDFactory;
import edu.teco.dnd.util.UniqueUUIDFactory;

@RunWith(MockitoJUnitRunner.class)
public class ResponseFutureManagerTest {
	private UUID sourceUUID1;
	private UUID sourceUUID2;
	@Mock
	private Response response1;
	@Mock
	private Response response2;

	@Mock
	private Throwable cause;

	private ResponseFutureManager manager;

	@Before
	public void setup() {
		final UUIDFactory uuidFactory = new UniqueUUIDFactory();
		sourceUUID1 = uuidFactory.createUUID();
		sourceUUID2 = uuidFactory.createUUID();

		when(response1.getSourceUUID()).thenReturn(sourceUUID1);
		when(response2.getSourceUUID()).thenReturn(sourceUUID2);

		manager = new ResponseFutureManager();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateResponseFutureNull() {
		manager.createResponseFuture(null);
	}

	@Test
	public void testCreateResponseFuture() {
		final ResponseFutureNotifier notifier = manager.createResponseFuture(sourceUUID1);

		assertNotNull(notifier);
		assertThat(notifier, responseFutureNotifierFor(sourceUUID1));
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateResponseFutureTwice() {
		manager.createResponseFuture(sourceUUID1);
		manager.createResponseFuture(sourceUUID1);
	}

	@Test
	public void testSetSuccess() {
		final ResponseFutureNotifier notifier = manager.createResponseFuture(sourceUUID1);

		manager.setSuccess(response1);

		assertThat(notifier, hasResult(response1));
	}

	@Test
	public void testSetFailure() {
		final ResponseFutureNotifier notifier = manager.createResponseFuture(sourceUUID1);

		manager.setFailure(sourceUUID1, cause);

		assertThat(notifier, hasCause(cause));
	}

	@Test
	public void testSetSuccessTwoResponses() {
		final ResponseFutureNotifier notifier1 = manager.createResponseFuture(sourceUUID1);
		final ResponseFutureNotifier notifier2 = manager.createResponseFuture(sourceUUID2);

		manager.setSuccess(response1);
		manager.setSuccess(response2);

		assertThat(notifier1, hasResult(response1));
		assertThat(notifier2, hasResult(response2));
	}

	@Test
	public void testSetFailureTwoResponses() {
		final ResponseFutureNotifier notifier1 = manager.createResponseFuture(sourceUUID1);
		final ResponseFutureNotifier notifier2 = manager.createResponseFuture(sourceUUID2);
		final Throwable cause2 = mock(Throwable.class);

		manager.setFailure(sourceUUID1, cause);
		manager.setFailure(sourceUUID2, cause2);

		assertThat(notifier1, hasCause(cause));
		assertThat(notifier2, hasCause(cause2));
	}

	@Test
	public void testSetSuccessNoSideeffects() {
		manager.createResponseFuture(sourceUUID1);
		final ResponseFutureNotifier notifier2 = manager.createResponseFuture(sourceUUID2);

		manager.setSuccess(response1);

		assertThat(notifier2, is(not(done())));
	}

	@Test
	public void testSetFailureNoSideeffects() {
		manager.createResponseFuture(sourceUUID1);
		final ResponseFutureNotifier notifier2 = manager.createResponseFuture(sourceUUID2);

		manager.setFailure(sourceUUID1, cause);

		assertThat(notifier2, is(not(done())));
	}

	private static ResponseFutureNotifierFor responseFutureNotifierFor(final UUID sourceUUID) {
		return new ResponseFutureNotifierFor(sourceUUID);
	}

	private static class ResponseFutureNotifierFor extends TypeSafeMatcher<ResponseFutureNotifier> {
		private final UUID sourceUUID;

		private ResponseFutureNotifierFor(final UUID sourceUUID) {
			this.sourceUUID = sourceUUID;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("a ResponseFutureNotifier for source UUID ");
			description.appendValue(sourceUUID);
		}

		@Override
		public boolean matchesSafely(ResponseFutureNotifier notifier) {
			if (sourceUUID == null) {
				return notifier.getSourceUUID() == null;
			} else {
				return sourceUUID.equals(notifier.getSourceUUID());
			}
		}

	}
}
