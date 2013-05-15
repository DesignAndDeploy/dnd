package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.teco.dnd.blocks.ConnectionTarget;
import edu.teco.dnd.blocks.InvalidFunctionBlockException;
import edu.teco.dnd.blocks.Output;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests methods related to the inputs of FunctionBlock.
 * 
 * @author philipp
 */
public class TestFunctionBlockOutputs {
	/**
	 * The names of the outputs defined in MockFunctionBlock.
	 */
	private final Set<String> mockOutputNames = new HashSet<>();

	/**
	 * The MockFunctionBlock used in tests.
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
	 * Initializes the set containing the names of the outputs.
	 */
	@Before
	public final void initSet() {
		mockOutputNames.clear();
		mockOutputNames.add("stringOutput");
		mockOutputNames.add("integerOutput");
		mockOutputNames.add("booleanOutput");
	}

	/**
	 * Tests that the outputs of MockFunctionBlock are detected correctly.
	 * 
	 * @throws InvalidFunctionBlockException
	 *             if MockFunctionBlock is defined incorrectly
	 */
	@Test
	public final void testOutputsMock() throws InvalidFunctionBlockException {
		Map<String, Output<?>> outputs = mockFunctionBlock.getOutputs();
		assertNotNull(outputs);
		assertEquals(mockOutputNames, outputs.keySet());
		List<Output<?>> outs = new ArrayList<>(outputs.values());
		for (int i = 0; i < outs.size(); i++) {
			assertNotNull(outs.get(i));
			for (int j = i + 1; j < outs.size(); j++) {
				assertNotSame(outs.get(i), outs.get(j));
			}
		}
	}

	/**
	 * Tests that the type of a String output is set correctly.
	 * 
	 * @throws InvalidFunctionBlockException
	 *             if MockFunctionBlock is defined incorrectly
	 */
	@Test
	public final void testOutputString() throws InvalidFunctionBlockException {
		assertEquals(String.class, mockFunctionBlock.getOutputs().get("stringOutput").getType());
	}

	/**
	 * Tests that the type of an Integer output is set correctly.
	 * 
	 * @throws InvalidFunctionBlockException
	 *             if MockFunctionBlock is defined incorrectly
	 */
	@Test
	public final void testOutputInteger() throws InvalidFunctionBlockException {
		assertEquals(Integer.class, mockFunctionBlock.getOutputs().get("integerOutput").getType());
	}

	/**
	 * Tests that the type of a Boolean output is set correctly.
	 * 
	 * @throws InvalidFunctionBlockException
	 *             if MockFunctionBlock is defined incorrectly
	 */
	@Test
	public final void testOutputBoolean() throws InvalidFunctionBlockException {
		assertEquals(Boolean.class, mockFunctionBlock.getOutputs().get("booleanOutput").getType());
	}

	/**
	 * Tests that the outputs of EmptyFunctionBlock are detected correctly.
	 * 
	 * @throws InvalidFunctionBlockException
	 *             if EmptyFunctionBlock is defined incorrectly
	 */
	@Test
	public final void testOutputsEmpty() throws InvalidFunctionBlockException {
		assertEquals(new HashMap<String, ConnectionTarget>(), new EmptyFunctionBlock("id").getOutputs());
	}

	/**
	 * tests that the outputs of DerivedFunctionBlock are detected correctly.
	 * 
	 * @throws InvalidFunctionBlockException
	 *             if MockFunctionBlock is defined incorrectly
	 */
	@Test
	public final void testOutputsDerived() throws InvalidFunctionBlockException {
		Set<String> expectedNames = new HashSet<>(mockOutputNames);
		expectedNames.add("output");
		Map<String, Output<?>> outputs = new DerivedFunctionBlock().getOutputs();
		assertNotNull(outputs);
		assertEquals(expectedNames, outputs.keySet());
		List<Output<?>> outs = new ArrayList<>(outputs.values());
		for (int i = 0; i < outs.size(); i++) {
			assertNotNull(outs.get(i));
			for (int j = i + 1; j < outs.size(); j++) {
				assertNotSame(outs.get(i), outs.get(j));
			}
		}
	}
}
