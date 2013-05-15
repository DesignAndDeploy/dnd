package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.ConnectionTarget;
import edu.teco.dnd.blocks.Output;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests Output.
 */
public class TestOutput {
	/**
	 * FunctionBlock used for testing.
	 */
	private MockFunctionBlock mockFunctionBlock;

	/**
	 * Output used for testing.
	 */
	private Output<String> testOutput;

	/**
	 * Contains multiple ConnectionTargets, including stringInput of mockFunctionBlock.
	 */
	private Set<ConnectionTarget> connectionTargets;

	/**
	 * Initializes object used for testing.
	 */
	@Before
	public final void init() {
		mockFunctionBlock = new MockFunctionBlock();
		testOutput = new Output<>("test");
		testOutput.setType(String.class);
		connectionTargets = new HashSet<>();
		connectionTargets.add(mockFunctionBlock.getConnectionTargets().get("stringInput"));
		connectionTargets.add(new MockFunctionBlock().getConnectionTargets().get("stringInput"));
		connectionTargets.add(new MockFunctionBlock().getConnectionTargets().get("stringInput"));
	}

	/**
	 * Makes sure that the name is stored correctly.
	 */
	@Test
	public final void testName() {
		assertEquals("test", testOutput.getName());
	}

	/**
	 * Makes sure that the type is stored correctly.
	 */
	@Test
	public final void testType() {
		assertEquals(String.class, testOutput.getType());
	}

	/**
	 * Makes sure that the type defaults to null.
	 */
	@Test
	public final void testTypeNull() {
		assertNull(new Output<String>("test").getType());
	}

	/**
	 * Makes sure that addConnection works correctly.
	 */
	@Test
	public final void testAddConnection() {
		Set<ConnectionTarget> expectedResult = new HashSet<>();
		expectedResult.add(mockFunctionBlock.getConnectionTargets().get("stringInput"));
		testOutput.addConnection(mockFunctionBlock.getConnectionTargets().get("stringInput"));
		assertEquals(expectedResult, testOutput.getConnectedTargets());
	}

	/**
	 * Makes sure that addConnection works correctly for multiple connections.
	 */
	@Test
	public final void testAddConnectionMultiple() {
		for (ConnectionTarget ct : connectionTargets) {
			testOutput.addConnection(ct);
		}
		assertEquals(connectionTargets, testOutput.getConnectedTargets());
	}

	/**
	 * Makes sure that removeConnection works correctly.
	 */
	@Test
	public final void testRemoveConnection() {
		for (ConnectionTarget ct : connectionTargets) {
			testOutput.addConnection(ct);
		}
		testOutput.removeConnection(mockFunctionBlock.getConnectionTargets().get("stringInput"));
		connectionTargets.remove(mockFunctionBlock.getConnectionTargets().get("stringInput"));
		assertEquals(connectionTargets, testOutput.getConnectedTargets());
	}

	/**
	 * Tests setValue.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testSetValue() throws AssignmentException {
		testOutput.addConnection(mockFunctionBlock.getConnectionTargets().get("stringInput"));
		testOutput.setValue("test");
		mockFunctionBlock.getConnectionTargets().get("stringInput").update();
		assertEquals("test", mockFunctionBlock.getStringInputValue());
	}

	/**
	 * Test setValue with multiple ConnectionTargets.
	 * 
	 * @throws AssignmentException
	 *             if assigning fails
	 */
	@Test
	public final void testSetValueMultiple() throws AssignmentException {
		for (ConnectionTarget ct : connectionTargets) {
			testOutput.addConnection(ct);
		}
		testOutput.setValue("test");
		mockFunctionBlock.getConnectionTargets().get("stringInput").update();
		assertEquals("test", mockFunctionBlock.getStringInputValue());
	}

	/**
	 * Tests that isCompatible works correctly.
	 */
	@Test
	public final void testIsCompatible() {
		assertTrue(testOutput.isCompatible(mockFunctionBlock.getConnectionTargets().get("stringInput")));
	}

	/**
	 * Tests that isCompatible returns false for incompatible ConnectionTargets.
	 */
	@Test
	public final void testIsCompatibleIncompatible() {
		assertFalse(testOutput.isCompatible(mockFunctionBlock.getConnectionTargets().get("integerInput")));
	}
}
