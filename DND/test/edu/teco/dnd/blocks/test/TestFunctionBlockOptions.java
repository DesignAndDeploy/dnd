package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.RetrievementException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests methods related to the options of FunctionBlock.
 * 
 * @author philipp
 * 
 */
public class TestFunctionBlockOptions {
	/**
	 * The names and types of the options defined in MockFunctionBlock.
	 */
	private final Map<String, Type> mockOptions = new HashMap<>();

	/**
	 * MockFunctionBlock used in the tests.
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
	 * Initializes the Map of the options defined in MockFunctionBlock.
	 */
	@Before
	public final void initMap() {
		mockOptions.clear();
		mockOptions.put("integerOption", Integer.class);
		mockOptions.put("stringOption", String.class);
	}

	/**
	 * Tests that MockFunctionBlock's options are detected correctly.
	 */
	@Test
	public final void testOptionsMock() {
		assertEquals(mockOptions, mockFunctionBlock.getOptions());
	}

	/**
	 * Tests that the options of EmptyFunctionBlock are detected correctly.
	 */
	@Test
	public final void testOptionsEmpty() {
		assertEquals(new HashMap<String, Type>(), new EmptyFunctionBlock("id").getOptions());
	}

	/**
	 * Tests that the options of DerivedFunctionBlock are detected correctly.
	 */
	@Test
	public final void testOptionsDerived() {
		Map<String, Type> expectedNames = new HashMap<>(mockOptions);
		expectedNames.put("option", Boolean.class);
		assertEquals(expectedNames, new DerivedFunctionBlock().getOptions());
	}

	/**
	 * Tests that retrieving an Integer option works correctly.
	 * 
	 * @throws RetrievementException
	 *             if getOption doesn't work as intended this exception may be thrown
	 */
	@Test
	public final void testGetOptionInteger() throws RetrievementException {
		assertEquals((Integer) 0, mockFunctionBlock.getOption("integerOption"));
	}

	/**
	 * Tests that retrieving a String option works correctly.
	 * 
	 * @throws RetrievementException
	 *             if getOption doesn't work as intended this exception may be thrown
	 */
	@Test
	public final void testGetOptionString() throws RetrievementException {
		assertEquals("foobar", mockFunctionBlock.getOption("stringOption"));
	}

	/**
	 * Tests that retrieving a derived option works correctly.
	 */
	@Test
	public final void testGetOptionDerived() throws RetrievementException {
		assertEquals("foobar", new DerivedFunctionBlock().getOption("stringOption"));
	}

	/**
	 * Tests that an IllegalArgumentException is thrown if an invalid name is passed to getOption.
	 * 
	 * @throws RetrievementException
	 *             if getOption doesn't work as intended this exception may be thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testGetOptionInvalid() throws RetrievementException {
		mockFunctionBlock.getOption("invalid");
	}

	/**
	 * Tests that an IllegalArgumentException is thrown if null is passed to getOption.
	 * 
	 * @throws RetrievementException
	 *             if getOption doesn't work as intended this exception may be thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testGetOptionNull() throws RetrievementException {
		mockFunctionBlock.getOption(null);
	}

	/**
	 * Tests that setOption works correctly.
	 * 
	 * @throws AssignmentException
	 *             if setOption doesn't work as intended this exception may be thrown
	 * @throws RetrievementException
	 *             if getOption doesn't work as intended this exception may be thrown
	 */
	@Test
	public final void testSetOption() throws AssignmentException, RetrievementException {
		mockFunctionBlock.setOption("integerOption", 1);
		assertEquals((Integer) 1, mockFunctionBlock.getOption("integerOption"));
	}

	@Test
	public final void testSetOptionDerived() throws RetrievementException, AssignmentException {
		FunctionBlock derivedBlock = new DerivedFunctionBlock();
		derivedBlock.setOption("integerOption", 1);
		assertEquals((Integer) 1, derivedBlock.getOption("integerOption"));
	}

	/**
	 * Tests that setOption throws an IllegalArgumentException if a value of an invalid type is passed.
	 * 
	 * @throws AssignmentException
	 *             if setOption doesn't work as intended this exception may be thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testSetOptionInvalidType() throws AssignmentException {
		mockFunctionBlock.setOption("integerOption", "string");
	}

	/**
	 * Tests that setOption throws an IllegalArgumentException if an invalid name is passed.
	 * 
	 * @throws AssignmentException
	 *             if setOption doesn't work as intended this exception may be thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testSetOptionInvalidName() throws AssignmentException {
		mockFunctionBlock.setOption("foobar", 1);
	}

	/**
	 * Tests that setOption throws an IllegalArgumentException if null is passed for the name.
	 * 
	 * @throws AssignmentException
	 *             if setOption doesn't work as intended this exception may be thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testSetOptionNameNull() throws AssignmentException {
		mockFunctionBlock.setOption(null, 1);
	}

	/**
	 * Tests that setOption works correctly if the value is null.
	 * 
	 * @throws AssignmentException
	 *             if setOption doesn't work as intended this exception may be thrown
	 * @throws RetrievementException
	 *             if getOption doesn't work as intended this exception may be thrown
	 */
	@Test
	public final void testSetOptionValueNull() throws AssignmentException, RetrievementException {
		mockFunctionBlock.setOption("integerOption", null);
		assertNull(mockFunctionBlock.getOption("integerOption"));
	}

	/**
	 * Tests that setting an option doesn't change other options.
	 * 
	 * @throws AssignmentException
	 *             if setOption doesn't work as intended this exception may be thrown
	 * @throws RetrievementException
	 *             if getOption doesn't work as intended this exception may be thrown
	 */
	@Test
	public final void setOptionUnrelated() throws AssignmentException, RetrievementException {
		mockFunctionBlock.setOption("stringOption", "foobar");
		mockFunctionBlock.setOption("integerOption", 1);
		assertEquals("foobar", mockFunctionBlock.getOption("stringOption"));
	}
}
