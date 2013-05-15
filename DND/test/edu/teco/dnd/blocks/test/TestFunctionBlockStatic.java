package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import edu.teco.dnd.blocks.FunctionBlock;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests static methods of FunctionBlock.
 * 
 * @author philipp
 * 
 */
public class TestFunctionBlockStatic {
	/**
	 * The inputs defined in {@link MockFunctionBlock}.
	 */
	private final Set<Field> inputsMock = new HashSet<>();

	/**
	 * The outputs defined in {@link MockFunctionBlock}.
	 */
	private final Set<Field> outputsMock = new HashSet<>();

	/**
	 * The options defined in {@link MockFunctionBlock}.
	 */
	private final Set<Field> optionsMock = new HashSet<>();

	/**
	 * Initializes the sets containing the inputs, outputs and options of MockFunctionBlock.
	 * 
	 * @throws NoSuchFieldException
	 *             if a field is missing
	 */
	@Before
	public final void initSets() throws NoSuchFieldException {
		inputsMock.clear();
		inputsMock.add(MockFunctionBlock.class.getDeclaredField("stringInput"));
		inputsMock.add(MockFunctionBlock.class.getDeclaredField("integerInput"));
		inputsMock.add(MockFunctionBlock.class.getDeclaredField("unqueuedInput"));
		inputsMock.add(MockFunctionBlock.class.getDeclaredField("queuedInput"));
		inputsMock.add(MockFunctionBlock.class.getDeclaredField("unqueuedNewOnlyInput"));
		inputsMock.add(MockFunctionBlock.class.getDeclaredField("queuedNewOnlyInput"));

		outputsMock.clear();
		outputsMock.add(MockFunctionBlock.class.getDeclaredField("stringOutput"));
		outputsMock.add(MockFunctionBlock.class.getDeclaredField("integerOutput"));
		outputsMock.add(MockFunctionBlock.class.getDeclaredField("booleanOutput"));

		optionsMock.clear();
		optionsMock.add(MockFunctionBlock.class.getDeclaredField("integerOption"));
		optionsMock.add(MockFunctionBlock.class.getDeclaredField("stringOption"));
	}

	/**
	 * Tests that the inputs of MockFunctionBlock are detected correctly.
	 */
	@Test
	public final void testInputMock() {
		assertEquals(inputsMock, FunctionBlock.getInputs(MockFunctionBlock.class));
	}

	/**
	 * Tests that the inputs of DerivedFunctionBlock are detected correctly.
	 * 
	 * @throws NoSuchFieldException
	 *             if a field is missing
	 */
	@Test
	public final void testInputDerived() throws NoSuchFieldException {
		Set<Field> expectedResult = new HashSet<Field>(inputsMock);
		expectedResult.add(DerivedFunctionBlock.class.getDeclaredField("input"));
		assertEquals(expectedResult, FunctionBlock.getInputs(DerivedFunctionBlock.class));
	}

	/**
	 * Tests that the inputs of EmptyFunctionBlock are detected correctly.
	 */
	@Test
	public final void testInputEmpty() {
		assertEquals(new HashSet<Field>(), FunctionBlock.getInputs(EmptyFunctionBlock.class));
	}

	/**
	 * Tests that the getInputs throws an IllegalArgumentException if null is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testInputNull() {
		FunctionBlock.getInputs(null);
	}

	/**
	 * Tests that the outputs of MockFunctionBlock are detected correctly.
	 */
	@Test
	public final void testOutputMock() {
		assertEquals(outputsMock, FunctionBlock.getOutputs(MockFunctionBlock.class));
	}

	/**
	 * Tests that the outputs of EmptyFunctionBlock are detected correctly.
	 */
	@Test
	public final void testOutputEmpty() {
		assertEquals(new HashSet<Field>(), FunctionBlock.getOutputs(EmptyFunctionBlock.class));
	}

	/**
	 * Tests that getOuputs throws an IllegalArgumentException if null is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testOutputNull() {
		FunctionBlock.getOutputs(null);
	}

	/**
	 * Tests that the outputs of DerivedFunctionBlock are detected correctly.
	 * 
	 * @throws NoSuchFieldException
	 *             if a field is missing
	 */
	@Test
	public final void testOutputDerived() throws NoSuchFieldException {
		Set<Field> expectedResult = new HashSet<Field>(outputsMock);
		expectedResult.add(DerivedFunctionBlock.class.getDeclaredField("output"));
		assertEquals(expectedResult, FunctionBlock.getOutputs(DerivedFunctionBlock.class));
	}

	/**
	 * Tests that the options of MockFunctionBlock are detected correctly.
	 */
	@Test
	public final void testOptionsMock() {
		assertEquals(optionsMock, FunctionBlock.getOptions(MockFunctionBlock.class));
	}

	/**
	 * Tests that the options of DerivedFunctionBlock are detected correctly.
	 * 
	 * @throws NoSuchFieldException
	 *             if a field is missing
	 */
	@Test
	public final void testOptionDerived() throws NoSuchFieldException {
		Set<Field> expectedResult = new HashSet<>(optionsMock);
		expectedResult.add(DerivedFunctionBlock.class.getDeclaredField("option"));
		assertEquals(expectedResult, FunctionBlock.getOptions(DerivedFunctionBlock.class));
	}

	/**
	 * Tests that the options of EmptyFunctionBlock are detected correctly.
	 */
	@Test
	public final void testOptionEmpty() {
		assertEquals(new HashSet<Field>(), FunctionBlock.getOptions(EmptyFunctionBlock.class));
	}

	/**
	 * Tests that getOptions throws an IllegalArgumentException if null is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testOptionsNull() {
		FunctionBlock.getOptions(null);
	}
}
