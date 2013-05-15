package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.teco.dnd.blocks.SystemTimer;

import org.junit.Test;

/**
 * Tests SystemTimer.
 * 
 * @author philipp
 */
public class TestSystemTimer {
	/**
	 * Must be large enough so check can be called between two ticks but should be as small as possible to
	 * make tests faster.
	 */
	private static final long TEST_TIME = 100;

	/**
	 * Tests that the constructor works correctly.
	 */
	@Test
	public final void testConstructor() {
		new SystemTimer(1L);
	}

	/**
	 * Tests that the constructor works correctly for large numbers.
	 */
	@Test
	public final void testConstructorLarge() {
		new SystemTimer(Long.MAX_VALUE);
	}

	/**
	 * Tests that the constructor throws an exception if 0 is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testConstructorZero() {
		new SystemTimer(0L);
	}

	/**
	 * Tests that the constructor throws an exception if a negative number is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testConstructorNegative() {
		new SystemTimer(-1L);
	}

	/**
	 * Tests that check returns false if not enough time has passed.
	 */
	@Test
	public final void testCheckEarly() {
		SystemTimer timer = new SystemTimer(Long.MAX_VALUE);
		assertFalse(timer.check());
	}

	/**
	 * Tests that check returns true if enough time has passed.
	 * 
	 * @throws InterruptedException
	 *             if the Thread is interrupted
	 */
	@Test
	public final void testCheck() throws InterruptedException {
		SystemTimer timer = new SystemTimer(1L);
		Thread.sleep(1);
		assertTrue(timer.check());
	}

	/**
	 * Tests that check resest the timer.
	 */
	@Test
	public final void testCheckReset() {
		SystemTimer timer = new SystemTimer(TEST_TIME);
		while (!timer.check()) {
			try {
				Thread.sleep(TEST_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		assertFalse(timer.check());
	}

	/**
	 * Tests that the time to the next tick is positive.
	 */
	@Test
	public final void testTimeToNextTick() {
		assertTrue(new SystemTimer(TEST_TIME).getTimeToNextTick() >= 0L);
	}

	/**
	 * Tests that the time to the next tick is positive for a small interval value.
	 */
	@Test
	public final void testTimeToNextTickSmall() {
		assertTrue(new SystemTimer(1).getTimeToNextTick() >= 0L);
	}
}
