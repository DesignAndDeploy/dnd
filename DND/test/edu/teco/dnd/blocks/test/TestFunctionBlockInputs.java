package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.teco.dnd.blocks.ConnectionTarget;
import edu.teco.dnd.blocks.NewValueConnectionTargetDecorator;
import edu.teco.dnd.blocks.QueuedConnectionTarget;
import edu.teco.dnd.blocks.SimpleConnectionTarget;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests methods related to the inputs of FunctionBlock.
 * 
 * @author philipp
 * 
 */
public class TestFunctionBlockInputs {
	/**
	 * Contains the names of the inputs defined in MockFunctionBlock.
	 */
	private final Set<String> mockInputNames = new HashSet<>();

	/**
	 * The MockFunctionBlock used for testing.
	 */
	private MockFunctionBlock mockFunctionBlock;

	/**
	 * Initializes the MockFunctionBlock.
	 */
	@Before
	public final void initFunctionBlocks() {
		mockFunctionBlock = new MockFunctionBlock();
	}

	/**
	 * Initializes the set containing the names of the inputs.
	 */
	@Before
	public final void initSet() {
		mockInputNames.clear();
		mockInputNames.add("stringInput");
		mockInputNames.add("integerInput");
		mockInputNames.add("unqueuedInput");
		mockInputNames.add("queuedInput");
		mockInputNames.add("unqueuedNewOnlyInput");
		mockInputNames.add("queuedNewOnlyInput");
	}

	/**
	 * Tests that the ConnectionTargets are generated correctly for MockFunctionBlock.
	 */
	@Test
	public final void testConnectionTargetsMock() {
		Map<String, ConnectionTarget> connectionTargets = mockFunctionBlock.getConnectionTargets();
		assertNotNull(connectionTargets);
		assertEquals(mockInputNames, connectionTargets.keySet());
		List<ConnectionTarget> cts = new ArrayList<>(connectionTargets.values());
		for (int i = 0; i < cts.size(); i++) {
			assertNotNull(cts.get(i));
			for (int j = i + 1; j < cts.size(); j++) {
				assertNotSame(cts.get(i), cts.get(j));
			}
		}
	}

	/**
	 * Tests that unqueued inputs get the right type of ConnectionTarget.
	 */
	@Test
	public final void testConnectionTargetUnqueued() {
		assertTrue(mockFunctionBlock.getConnectionTargets().get("unqueuedInput") instanceof SimpleConnectionTarget);
	}

	/**
	 * Tests that queued inputs get the right type of ConnectionTarget.
	 */
	@Test
	public final void testConnectionTargetQueued() {
		assertTrue(mockFunctionBlock.getConnectionTargets().get("queuedInput") instanceof QueuedConnectionTarget);
	}

	/**
	 * Tests that unqueued newOnly inputs get the right type of ConnectionTarget.
	 */
	@Test
	public final void testConnectionTargetUnqueuedNewOnly() {
		ConnectionTarget ct = mockFunctionBlock.getConnectionTargets().get("unqueuedNewOnlyInput");
		assertTrue(ct instanceof NewValueConnectionTargetDecorator);
	}

	/**
	 * Tests that queued newOnly inputs get the right type of ConnectionTarget.
	 */
	@Test
	public final void testConnectionTargetQueuedNewOnly() {
		ConnectionTarget ct = mockFunctionBlock.getConnectionTargets().get("queuedNewOnlyInput");
		assertTrue(ct instanceof NewValueConnectionTargetDecorator);
	}

	/**
	 * Tests that unqueued newOnly inputs decorates the right type of ConnectionTarget.
	 */
	@Test
	public final void testConnectionTargetUnqueuedNewOnlyInner() {
		NewValueConnectionTargetDecorator ct = (NewValueConnectionTargetDecorator) mockFunctionBlock
				.getConnectionTargets().get("unqueuedNewOnlyInput");
		assertTrue(ct.getDecoratedConnectionTarget() instanceof SimpleConnectionTarget);
	}

	/**
	 * Tests that queued newOnly inputs decorates the right type of ConnectionTarget.
	 */
	@Test
	public final void testConnectionTargetQueuedNewOnlyInner() {
		NewValueConnectionTargetDecorator ct = (NewValueConnectionTargetDecorator) mockFunctionBlock
				.getConnectionTargets().get("queuedNewOnlyInput");
		assertTrue(ct.getDecoratedConnectionTarget() instanceof QueuedConnectionTarget);
	}

	/**
	 * Tests that the ConnectionTargets are generated correctly for EmptyFunctionBlock.
	 */
	@Test
	public final void testConnectionTargetsEmpty() {
		assertEquals(new HashMap<String, ConnectionTarget>(),
				new EmptyFunctionBlock("id").getConnectionTargets());
	}

	/**
	 * Tests that the ConnectionTargets are generated correctly for DerivedFunctionBlock.
	 */
	@Test
	public final void testConnectionTargetsDerived() {
		Set<String> expectedNames = new HashSet<>(mockInputNames);
		expectedNames.add("input");
		Map<String, ConnectionTarget> connectionTargets = new DerivedFunctionBlock().getConnectionTargets();
		assertEquals(expectedNames, connectionTargets.keySet());
		List<ConnectionTarget> cts = new ArrayList<>(connectionTargets.values());
		for (int i = 0; i < cts.size(); i++) {
			assertNotNull(cts.get(i));
			for (int j = i + 1; j < cts.size(); j++) {
				assertNotSame(cts.get(i), cts.get(j));
			}
		}
	}
}
