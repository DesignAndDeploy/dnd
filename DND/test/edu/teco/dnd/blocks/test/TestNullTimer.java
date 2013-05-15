package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import edu.teco.dnd.blocks.NullTimer;

import org.junit.Test;

/**
 * Tests NullTimer.
 * 
 * @author philipp
 */
public class TestNullTimer {
	/**
	 * Tests that the singleton is not null.
	 */
	@Test
	public final void testSingletonNotNull() {
		assertNotNull(NullTimer.getInstance());
	}

	/**
	 * Tests that a singleton is returned.
	 */
	@Test
	public final void testSingleton() {
		assertSame(NullTimer.getInstance(), NullTimer.getInstance());
	}

	/**
	 * Tests that NullTimer never ticks.
	 */
	@Test
	public final void testCheck() {
		assertFalse(NullTimer.getInstance().check());
	}

	/**
	 * Tests that the time to the next tick is negative.
	 */
	@Test
	public final void testTimeToNextTick() {
		assertTrue(NullTimer.getInstance().getTimeToNextTick() < 0);
	}
}
