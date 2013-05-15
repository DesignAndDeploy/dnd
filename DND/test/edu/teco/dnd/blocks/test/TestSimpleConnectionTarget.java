package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.ConnectionTarget;
import edu.teco.dnd.blocks.SimpleConnectionTarget;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests SimpleConnectionTarget.
 * 
 * @author philipp
 */
public class TestSimpleConnectionTarget {
	/**
	 * MockFunctionBlock used for testing.
	 */
	private MockFunctionBlock mockFunctionBlock;

	/**
	 * intgerInput of {@link #mockFunctionBlock}.
	 */
	private ConnectionTarget integerConnectionTarget;

	/**
	 * Initializes objects for testing.
	 */
	@Before
	public final void init() {
		mockFunctionBlock = new MockFunctionBlock();
		integerConnectionTarget = mockFunctionBlock.getConnectionTargets().get("integerInput");
	}

	/**
	 * Tests that an IllegalArgumentException is thrown if null is passed for the FunctionBlock.
	 * 
	 * @throws NoSuchFieldException
	 *             if getting the field is not allowed
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testConstructorFunctionBlockNull() throws NoSuchFieldException {
		new SimpleConnectionTarget("test", null, MockFunctionBlock.class.getDeclaredField("integerInput"));
	}

	/**
	 * Tests that an IllegalArgumentException is thrown if null is passed for the Field.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testConstructorFieldNull() {
		new SimpleConnectionTarget("test", mockFunctionBlock, null);
	}

	/**
	 * Tests that an IllegalArgumentException is thrown if a non serializable Field is passed.
	 * 
	 * @throws NoSuchFieldException
	 *             if getting the field is not allowed
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testConstructorFieldInvalid() throws NoSuchFieldException {
		new SimpleConnectionTarget("test", mockFunctionBlock,
				MockFunctionBlock.class.getDeclaredField("object"));
	}

	/**
	 * Tests that a new SimpleConnectionTarget is not dirty.
	 */
	@Test
	public final void testDirtyNew() {
		assertFalse(integerConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is marked dirty after setting a value.
	 */
	@Test
	public final void testDirty() {
		integerConnectionTarget.setValue(null);
		assertTrue(integerConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is marked dirty if two values have been set.
	 */
	@Test
	public final void testDirtyTwoValues() {
		integerConnectionTarget.setValue(null);
		integerConnectionTarget.setValue(1);
		assertTrue(integerConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is not dirty after an update.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testDirtyUpdate() throws AssignmentException {
		integerConnectionTarget.setValue(null);
		integerConnectionTarget.update();
		assertFalse(integerConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is dirty if a value is set after an update occurred.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testDirtyTwice() throws AssignmentException {
		integerConnectionTarget.setValue(null);
		integerConnectionTarget.update();
		integerConnectionTarget.setValue(1);
		assertTrue(integerConnectionTarget.isDirty());
	}

	/**
	 * Tests that setValue leaves the input unchanged.
	 */
	@Test
	public final void testSetUpdateUnchanged() {
		integerConnectionTarget.setValue(1);
		assertEquals(0, mockFunctionBlock.getIntegerInputValue());
	}

	/**
	 * Tests that the ConnectionTarget is not dirty if two values have been set and an update occurred.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testDirtyQueue() throws AssignmentException {
		integerConnectionTarget.setValue(null);
		integerConnectionTarget.setValue(1);
		integerConnectionTarget.update();
		assertFalse(integerConnectionTarget.isDirty());
	}

	/**
	 * Tests that update works correctly.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testUpdate() throws AssignmentException {
		integerConnectionTarget.setValue(1);
		integerConnectionTarget.update();
		assertEquals(1, mockFunctionBlock.getIntegerInputValue());
	}

	/**
	 * Tests that update works correctly if called twice.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testUpdateTwice() throws AssignmentException {
		integerConnectionTarget.setValue(1);
		integerConnectionTarget.update();
		integerConnectionTarget.setValue(0);
		integerConnectionTarget.update();
		assertEquals(0, mockFunctionBlock.getIntegerInputValue());
	}

	/**
	 * Tests that update works correctly if two values have been set.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testUpdateTwoValues() throws AssignmentException {
		integerConnectionTarget.setValue(0);
		integerConnectionTarget.setValue(1);
		integerConnectionTarget.update();
		assertEquals(1, mockFunctionBlock.getIntegerInputValue());
	}

	/**
	 * Tests that getType works correctly.
	 */
	@Test
	public final void testGetTypeInteger() {
		assertEquals(Integer.class, integerConnectionTarget.getType());
	}

	/**
	 * Tests that getType works correctly.
	 */
	@Test
	public final void testGetTypeString() {
		assertEquals(String.class, mockFunctionBlock.getConnectionTargets().get("stringInput").getType());
	}
}
