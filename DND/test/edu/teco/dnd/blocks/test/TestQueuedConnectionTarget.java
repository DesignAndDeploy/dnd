package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.ConnectionTarget;
import edu.teco.dnd.blocks.QueuedConnectionTarget;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests QueuedConnectionTarget.
 * 
 * @author philipp
 */
public class TestQueuedConnectionTarget {
	/**
	 * MockFunctionBlock used for testing.
	 */
	private MockFunctionBlock mockFunctionBlock;

	/**
	 * QueuedConnectionTarget belong to {@link #mockFunctionBlock} used for testing.
	 */
	private ConnectionTarget queuedConnectionTarget;

	/**
	 * Initializes objects used for testing.
	 */
	@Before
	public final void init() {
		mockFunctionBlock = new MockFunctionBlock();
		queuedConnectionTarget = mockFunctionBlock.getConnectionTargets().get("queuedInput");
	}

	/**
	 * Tests that the constructor throws an IllegalArgumentException if null is passed for the FunctionBlock.
	 * 
	 * @throws NoSuchFieldException
	 *             if getting a field fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testConstructorFunctionBlockNull() throws NoSuchFieldException {
		new QueuedConnectionTarget("test", null, MockFunctionBlock.class.getDeclaredField("queuedInput"));
	}

	/**
	 * Tests that the constructor throws an IllegalArgumentException if null is passed for the Field.
	 * 
	 * @throws NoSuchFieldException
	 *             if getting a field fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testConstructorFieldNull() throws NoSuchFieldException {
		new QueuedConnectionTarget("test", mockFunctionBlock, null);
	}

	/**
	 * Tests that the constructor throws an IllegalArgumentException if the Field is not Serializable.
	 * 
	 * @throws NoSuchFieldException
	 *             if getting a field fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testConstructorFieldInvalid() throws NoSuchFieldException {
		new QueuedConnectionTarget("test", mockFunctionBlock,
				MockFunctionBlock.class.getDeclaredField("object"));
	}

	/**
	 * Tests that a new QueuedConnectionTarget is not dirty.
	 */
	@Test
	public final void testDirtyNew() {
		assertFalse(queuedConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is marked dirty after setting a value.
	 */
	@Test
	public final void testDirty() {
		queuedConnectionTarget.setValue(null);
		assertTrue(queuedConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is marked dirty if two values have been set.
	 */
	@Test
	public final void testDirtyTwoValues() {
		queuedConnectionTarget.setValue(null);
		queuedConnectionTarget.setValue(true);
		assertTrue(queuedConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is not dirty after an update.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testDirtyUpdate() throws AssignmentException {
		queuedConnectionTarget.setValue(null);
		queuedConnectionTarget.update();
		assertFalse(queuedConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is dirty if a value is set after an update occurred.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testDirtyTwice() throws AssignmentException {
		queuedConnectionTarget.setValue(null);
		queuedConnectionTarget.update();
		queuedConnectionTarget.setValue(true);
		assertTrue(queuedConnectionTarget.isDirty());
	}

	/**
	 * Tests that setValue leaves the input unchanged.
	 */
	@Test
	public final void testSetUpdateUnchanged() {
		queuedConnectionTarget.setValue(true);
		assertEquals(false, mockFunctionBlock.getQueuedInputValue());
	}

	/**
	 * Tests that the ConnectionTarget is dirty if two values have been set but only one update occurred.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testDirtyQueue() throws AssignmentException {
		queuedConnectionTarget.setValue(null);
		queuedConnectionTarget.setValue(true);
		queuedConnectionTarget.update();
		assertTrue(queuedConnectionTarget.isDirty());
	}

	/**
	 * Tests that update works correctly.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testUpdate() throws AssignmentException {
		queuedConnectionTarget.setValue(true);
		queuedConnectionTarget.update();
		assertEquals(true, mockFunctionBlock.getQueuedInputValue());
	}

	/**
	 * Tests that update works correctly if called twice.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testUpdateTwice() throws AssignmentException {
		queuedConnectionTarget.setValue(null);
		queuedConnectionTarget.update();
		queuedConnectionTarget.setValue(true);
		queuedConnectionTarget.update();
		assertEquals(true, mockFunctionBlock.getQueuedInputValue());
	}

	/**
	 * Tests that update works correctly if called with two values.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testUpdateTwoValuesFirst() throws AssignmentException {
		queuedConnectionTarget.setValue(null);
		queuedConnectionTarget.setValue(true);
		queuedConnectionTarget.update();
		assertEquals(null, mockFunctionBlock.getQueuedInputValue());
	}

	/**
	 * Tests that update works correctly if called with two values.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testUpdateTwoValuesSecond() throws AssignmentException {
		queuedConnectionTarget.setValue(null);
		queuedConnectionTarget.setValue(true);
		queuedConnectionTarget.update();
		queuedConnectionTarget.update();
		assertEquals(true, mockFunctionBlock.getQueuedInputValue());
	}

	/**
	 * Tests that getType works correctly.
	 */
	@Test
	public final void testGetTypeBoolean() {
		assertEquals(Boolean.class, queuedConnectionTarget.getType());
	}

	/**
	 * Tests that getType works correctly.
	 * 
	 * @throws NoSuchFieldException
	 *             if getting a field fails
	 */
	@Test
	public final void testGetTypeInteger() throws NoSuchFieldException {
		assertEquals(Integer.class, new QueuedConnectionTarget("test", mockFunctionBlock,
				MockFunctionBlock.class.getDeclaredField("integerInput")).getType());
	}
}
