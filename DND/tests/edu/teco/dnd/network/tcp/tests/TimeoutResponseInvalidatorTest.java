package edu.teco.dnd.network.tcp.tests;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Calendar;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.network.tcp.ResponseFutureManager.ResponseFutureNotifier;
import edu.teco.dnd.network.tcp.TimeoutResponseInvalidator;

@RunWith(MockitoJUnitRunner.class)
public class TimeoutResponseInvalidatorTest {
	private static final long DELAY = 5;
	private static final TimeUnit UNIT = TimeUnit.SECONDS;
	
	@Mock
	private ResponseFutureNotifier responseFutureNotifier1;
	
	@Mock
	private ResponseFutureNotifier responseFutureNotifier2;
	
	private ScheduledExecutorService scheduledExecutorService;
	
	private TimeoutResponseInvalidator timeoutResponseInvalidator;
	
	@Before
	public void setUp() {
		scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
		timeoutResponseInvalidator = new TimeoutResponseInvalidator(scheduledExecutorService, DELAY, UNIT);
	}
	
	@After
	public void tearDown() {
		scheduledExecutorService.shutdownNow();
	}
	
	@Test
	public void testNotInvalidedBeforeTimeout() {
		timeoutResponseInvalidator.addTimeout(responseFutureNotifier1);
		
		verifyNoMoreInteractions(responseFutureNotifier1);
	}
	
	@Test
	public void testInvalidatedAfterTimeout() throws InterruptedException {
		timeoutResponseInvalidator.addTimeout(responseFutureNotifier1, 3, TimeUnit.SECONDS);
		
		final Calendar afterTimeout = Calendar.getInstance();
		afterTimeout.add(Calendar.SECOND, 3 + 1);
		final long afterTimeoutMS = afterTimeout.getTimeInMillis();
		
		long currentTimeMS = System.currentTimeMillis();
		while (currentTimeMS < afterTimeoutMS) {
            Thread.sleep(afterTimeoutMS - currentTimeMS);
			currentTimeMS = System.currentTimeMillis();
		}
		
		verify(responseFutureNotifier1).setFailure0(any(Throwable.class));
	}
	
	@Test
	public void testOtherNotInvalidated() throws InterruptedException {
		timeoutResponseInvalidator.addTimeout(responseFutureNotifier1);
		timeoutResponseInvalidator.addTimeout(responseFutureNotifier2, 1, TimeUnit.NANOSECONDS);
		
		Thread.sleep(100);
		
		verifyNoMoreInteractions(responseFutureNotifier1);
	}
}
