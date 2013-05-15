package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.ConnectionTarget;
import edu.teco.dnd.blocks.NewValueConnectionTargetDecorator;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests NewValueConnectionTargetDecorator.
 * 
 * @author philipp
 */
public class TestNewValueConnectionTargetDecorator {
	/**
	 * MockFunctionBlock used for testing.
	 */
	private MockFunctionBlock mockFunctionBlock;

	/**
	 * intgerInput of {@link #mockFunctionBlock}.
	 */
	private ConnectionTarget integerConnectionTarget;

	/**
	 * A NewValueConnectionTargetDecorator for {@link #integerConnectionTarget}.
	 */
	private NewValueConnectionTargetDecorator newValueConnectionTarget;

	/**
	 * Initializes objects for testing.
	 */
	@Before
	public final void init() {
		mockFunctionBlock = new MockFunctionBlock();
		integerConnectionTarget = mockFunctionBlock.getConnectionTargets().get("integerInput");
		newValueConnectionTarget = new NewValueConnectionTargetDecorator(integerConnectionTarget);
	}

	/**
	 * Tests that an IllegalArgumentException is thrown if null is passed for the ConnectionTarget.
	 */
	@Test(expected = NullPointerException.class)
	public final void testConstructorConnectionTargetNull() {
		new NewValueConnectionTargetDecorator(null);
	}

	/**
	 * Tests that a new SimpleConnectionTarget is not dirty.
	 */
	@Test
	public final void testDirtyNew() {
		assertFalse(newValueConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is marked dirty after setting a value.
	 */
	@Test
	public final void testDirty() {
		newValueConnectionTarget.setValue(null);
		assertTrue(newValueConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is marked dirty if two values have been set.
	 */
	@Test
	public final void testDirtyTwoValues() {
		newValueConnectionTarget.setValue(null);
		newValueConnectionTarget.setValue(1);
		assertTrue(newValueConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is not dirty after an update.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testDirtyUpdate() throws AssignmentException {
		newValueConnectionTarget.setValue(null);
		newValueConnectionTarget.update();
		assertFalse(newValueConnectionTarget.isDirty());
	}

	/**
	 * Tests that the ConnectionTarget is dirty if a value is set after an update occurred.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testDirtyTwice() throws AssignmentException {
		newValueConnectionTarget.setValue(null);
		newValueConnectionTarget.update();
		newValueConnectionTarget.setValue(1);
		assertTrue(newValueConnectionTarget.isDirty());
	}

	/**
	 * Tests that the decorated ConnectionTarget is not changed if the same value is set again.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testDirtySame() throws AssignmentException {
		newValueConnectionTarget.setValue(1);
		newValueConnectionTarget.update();
		newValueConnectionTarget.setValue(1);
		assertFalse(newValueConnectionTarget.isDirty());
	}

	/**
	 * Tests that the decorated ConnectionTarget is not changed if the same value is set again.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testDirtySameAfter() throws AssignmentException {
		newValueConnectionTarget.setValue(1);
		newValueConnectionTarget.setValue(1);
		newValueConnectionTarget.update();
		assertFalse(newValueConnectionTarget.isDirty());
	}

	/**
	 * Tests that getType works correctly.
	 */
	@Test
	public final void testGetTypeInteger() {
		assertEquals(Integer.class, newValueConnectionTarget.getType());
	}
}
