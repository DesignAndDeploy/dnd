package edu.teco.dnd.util.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFutureNotifierTest {
	public static final Integer TEST_VALUE = 42;
	
	public static final Throwable TEST_CAUSE = new Exception("cause");
	
	private TestDefaultFutureNotifier<Integer> notifier;

	@Mock private FutureListener<FutureNotifier<Integer>> listener1;
	
	@Mock private FutureListener<FutureNotifier<Integer>> listener2;
	
	@Before
	public void createNotifier() {
		notifier = new TestDefaultFutureNotifier<Integer>();
	}

	@Test
	public void testIsDoneBefore() {
		assertFalse(notifier.isDone());
	}
	
	@Test
	public void testIsSuccessBefore() {
		assertFalse(notifier.isSuccess());
	}
	
	@Test
	public void testCauseBefore() {
		assertNull(notifier.cause());
	}

	@Test
	public void testGetNowBefore() {
		assertNull(notifier.getNow());
	}

	@Test
	public void testSetSuccess() {
		assertTrue(notifier.setSuccess(TEST_VALUE));
	}

	@Test
	public void testIsDoneAfterSuccess() {
		assumeTrue(notifier.setSuccess(TEST_VALUE));
		assertTrue(notifier.isDone());
	}

	@Test
	public void testIsSuccessAfterSuccess() {
		assumeTrue(notifier.setSuccess(TEST_VALUE));
		assertTrue(notifier.isSuccess());
	}

	@Test
	public void testCauseAfterSuccess() {
		assumeTrue(notifier.setSuccess(TEST_VALUE));
		assertNull(notifier.cause());
	}

	@Test
	public void testGetNowAfterSuccess() {
		assumeTrue(notifier.setSuccess(TEST_VALUE));
		assertEquals(TEST_VALUE, notifier.getNow());
	}

	@Test
	public void testSetFailure() {
		assertTrue(notifier.setFailure(TEST_CAUSE));
	}

	@Test
	public void testIsDoneAfterFailure() {
		assumeTrue(notifier.setFailure(TEST_CAUSE));
		assertTrue(notifier.isDone());
	}

	@Test
	public void testIsSuccessAfterFailure() {
		assumeTrue(notifier.setFailure(TEST_CAUSE));
		assertFalse(notifier.isSuccess());
	}

	@Test
	public void testCauseAfterFailure() {
		assumeTrue(notifier.setFailure(TEST_CAUSE));
		assertEquals(TEST_CAUSE, notifier.cause());
	}

	@Test
	public void testGetNowAfterFailure() {
		assumeTrue(notifier.setFailure(TEST_CAUSE));
		assertNull(notifier.getNow());
	}
	
	@Test
	public void testListenerSuccess() throws Exception {
		notifier.addListener(listener1);
		assumeTrue(notifier.setSuccess(TEST_VALUE));
		verify(listener1).operationComplete(notifier);
	}
	
	@Test
	public void testListenerFailure() throws Exception {
		notifier.addListener(listener1);
		assumeTrue(notifier.setFailure(TEST_CAUSE));
		verify(listener1).operationComplete(notifier);
	}
	
	@Test
	public void testListenerAfterSuccess() throws Exception {
		assumeTrue(notifier.setSuccess(TEST_VALUE));
		notifier.addListener(listener1);
		verify(listener1).operationComplete(notifier);
	}
	
	@Test
	public void testListenerAfterFailure() throws Exception {
		assumeTrue(notifier.setFailure(TEST_CAUSE));
		notifier.addListener(listener1);
		verify(listener1).operationComplete(notifier);
	}
	
	@Test
	public void testTwoListenersSuccess() throws Exception {
		notifier.addListener(listener1);
		notifier.addListener(listener2);
		assumeTrue(notifier.setSuccess(TEST_VALUE));
		verify(listener1).operationComplete(notifier);
		verify(listener2).operationComplete(notifier);
	}
	
	@Test
	public void testTwoListenersFailure() throws Exception {
		notifier.addListener(listener1);
		notifier.addListener(listener2);
		assumeTrue(notifier.setFailure(TEST_CAUSE));
		verify(listener1).operationComplete(notifier);
		verify(listener2).operationComplete(notifier);
	}
	
	@Test
	public void testTwoListenersAfterSuccess() throws Exception {
		assumeTrue(notifier.setSuccess(TEST_VALUE));
		notifier.addListener(listener1);
		notifier.addListener(listener2);
		verify(listener1).operationComplete(notifier);
		verify(listener2).operationComplete(notifier);
	}
	
	@Test
	public void testTwoListenersAfterFailure() throws Exception {
		assumeTrue(notifier.setFailure(TEST_CAUSE));
		notifier.addListener(listener1);
		notifier.addListener(listener2);
		verify(listener1).operationComplete(notifier);
		verify(listener2).operationComplete(notifier);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveListenerSuccess() throws Exception {
		notifier.addListener(listener1);
		notifier.addListener(listener2);
		notifier.removeListener(listener1);
		assumeTrue(notifier.setSuccess(TEST_VALUE));
		verify(listener1, never()).operationComplete(any(FutureNotifier.class));
		verify(listener2).operationComplete(notifier);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveListenerFailure() throws Exception {
		notifier.addListener(listener1);
		notifier.addListener(listener2);
		notifier.removeListener(listener1);
		assumeTrue(notifier.setFailure(TEST_CAUSE));
		verify(listener1, never()).operationComplete(any(FutureNotifier.class));
		verify(listener2).operationComplete(notifier);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAddTwiceSuccess() throws Exception {
		notifier.addListener(listener1);
		notifier.addListener(listener1);
		notifier.addListener(listener2);
		notifier.removeListener(listener1);
		assumeTrue(notifier.setSuccess(TEST_VALUE));
		verify(listener1, never()).operationComplete(any(FutureNotifier.class));
		verify(listener2).operationComplete(notifier);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAddTwiceFailure() throws Exception {
		notifier.addListener(listener1);
		notifier.addListener(listener1);
		notifier.addListener(listener2);
		notifier.removeListener(listener1);
		assumeTrue(notifier.setFailure(TEST_CAUSE));
		verify(listener1, never()).operationComplete(any(FutureNotifier.class));
		verify(listener2).operationComplete(notifier);
	}
	
	@Test
	public void testDifferentType() {
		final TestDefaultFutureNotifier<String> otherNotifier = new TestDefaultFutureNotifier<String>();
		assumeTrue(otherNotifier.setSuccess("foo"));
		assertEquals("foo", otherNotifier.getNow());
	}
	
	private class TestDefaultFutureNotifier<T> extends DefaultFutureNotifier<T> {
		public boolean setSuccess(final T result) {
			return super.setSuccess(result);
		}
		
		public boolean setFailure(final Throwable cause) {
			return super.setFailure(cause);
		}
	}
}
