package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;

import edu.teco.dnd.messages.ApplicationValueMessage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppValueMessageTest {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public final static String MESSAGE_IDENTIFIER = "Value";

	/**
	 * ID of functionBlock whose Input is to be updated
	 */
	private String functionBlockID;

	/**
	 * String describing which input of the functionBlock receives a new value
	 */
	private String input;

	/**
	 * Value to be received by the input of the function block
	 */
	private Serializable value;

	/**
	 * message used for testing, initialized with parameters
	 */
	public ApplicationValueMessage message1;

	/**
	 * message used for testing, initalized with a tuple
	 */
	public ApplicationValueMessage message2;

	/**
	 * message used for testing, initialized with empty constructor
	 */
	public ApplicationValueMessage message3;

	/**
	 * tuple used for testing and initializing message2
	 */
	public ITuple tuple;

	@BeforeClass
	public static void initLights() throws ClassNotFoundException {
		TupleSpaceFactory.setFactory("lights.adapters.builtin.TupleSpaceFactory");
	}

	@Before
	public void init() {
		functionBlockID = "1337";
		input = "hello";
		value = 16;
		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(functionBlockID);
		tuple.addActual(input);
		tuple.addActual(value);

		message1 = new ApplicationValueMessage(functionBlockID, input, value);
		message2 = new ApplicationValueMessage(tuple);
		message3 = new ApplicationValueMessage();
	}

	/**
	 * tests if the messages were initialized correctly
	 */
	@Test
	public void initializationTest() {
		assertEquals(functionBlockID, message1.getFunctionBlockID());
		assertEquals(input, message1.getInput());
		assertEquals(value, message1.getValue());

		assertEquals(functionBlockID, message2.getFunctionBlockID());
		assertEquals(input, message2.getInput());
		assertEquals(value, message2.getValue());

		assertNull(message3.getFunctionBlockID());
		assertNull(message3.getInput());
		assertNull(message3.getValue());
	}

	/**
	 * tests the getTemplate function
	 */
	@Test
	public void templateTest() {
		assertTrue(message1.getTemplate().matches(tuple));
		assertTrue(message2.getTemplate().matches(tuple));
		assertTrue(message3.getTemplate().matches(tuple));
	}

	/**
	 * Tests the getting and setting of tuples
	 */
	@Test
	public void tupleTest() {
		assertTrue(tuple.matches(message1.getTuple()));
		assertTrue(tuple.matches(message2.getTuple()));

		message3.setTuple(tuple);
		assertTrue(tuple.matches(message3.getTuple()));

		String newfunctionBlockID = "ID 1338";
		String newinput = "bye";
		value = 2;

		ITuple newTuple = new Tuple();
		newTuple.addActual(MESSAGE_IDENTIFIER);
		newTuple.addActual(newfunctionBlockID);
		newTuple.addActual(newinput);
		newTuple.addActual(value);
		message3.setTuple(newTuple);
		message2.setTuple(newTuple);

		assertTrue(newTuple.matches(message2.getTuple()));
		assertTrue(newTuple.matches(message3.getTuple()));
	}

	/**
	 * Tests the exception in setTuple
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		tuple.addActual("too many args");
		message1.setTuple(tuple);
	}

	/**
	 * tests the getting and setting methods
	 */
	@Test
	public void setTest() {
		functionBlockID = "new block ID";
		input = "this is a new input";
		value = 999;

		message1.setFunctionBlockID(functionBlockID);
		message2.setInput(input);
		message3.setValue(value);

		assertEquals(functionBlockID, message1.getFunctionBlockID());
		assertEquals(input, message2.getInput());
		assertEquals(value, message3.getValue());
	}

}
