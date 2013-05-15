package edu.teco.dnd.messages.test;

import static edu.teco.dnd.messages.GlobalApplicationMessage.APPLICATIONID_INDEX;
import static edu.teco.dnd.messages.GlobalApplicationMessage.NAME_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;

import edu.teco.dnd.messages.GlobalApplicationMessage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the GlobalApplicationMessage.
 */
public class TestGlobalApplicationMessage {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Application";

	/**
	 * This attribute contains the name of an application.
	 */
	private String name;

	/**
	 * This attribute contains the ID of an application.
	 */
	private Integer applicationID;

	/**
	 * message used for testing.
	 */
	private GlobalApplicationMessage message;

	/**
	 * tuple used for testing and initializing.
	 */
	private ITuple tuple;

	/**
	 * Initializes the TupleSpaceFactory.
	 * 
	 * @throws ClassNotFoundException
	 *             if Class not Found
	 */
	@BeforeClass
	public static void initLights() throws ClassNotFoundException {
		TupleSpaceFactory.setFactory("lights.adapters.builtin.TupleSpaceFactory");
	}

	/**
	 * Initializes the tuple and messages for testing.
	 */
	@Before
	public void init() {
		name = "1337";
		applicationID = 2;

		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(name);
		tuple.addActual(applicationID);
	}

	/**
	 * Initializes the message with parameters and tests whether name was set correctly.
	 */
	@Test
	public void testname2param() {
		message = new GlobalApplicationMessage(name, applicationID);
		assertEquals(name, message.getName());
	}

	/**
	 * Initializes the message with parameters and tests whether applicationID was set correctly.
	 */
	@Test
	public void testapplicationID2param() {
		message = new GlobalApplicationMessage(name, applicationID);
		assertEquals(applicationID, message.getApplicationID());
	}

	/**
	 * Initializes the message without parameters and tests whether parameters are null.
	 */
	@Test
	public void testname0param() {
		message = new GlobalApplicationMessage();
		assertNull(message.getApplicationID());
		assertNull(message.getName());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether name was set correctly.
	 */
	@Test
	public void testnametuple() {
		message = new GlobalApplicationMessage(tuple);
		assertEquals(name, message.getName());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether applicationID was set correctly.
	 */
	@Test
	public void testapplicationIDtuple() {
		message = new GlobalApplicationMessage(tuple);
		assertEquals(applicationID, message.getApplicationID());
	}

	/**
	 * Initializes a message with a tuple and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTesttupleInit() {
		message = new GlobalApplicationMessage(tuple);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(name, message.getTuple().get(NAME_INDEX).getValue());
		assertEquals(applicationID, message.getTuple().get(APPLICATIONID_INDEX).getValue());
	}

	/**
	 * Initializes a message with two parameters and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTest2paramInit() {
		message = new GlobalApplicationMessage(name, applicationID);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(name, message.getTuple().get(NAME_INDEX).getValue());
		assertEquals(applicationID, message.getTuple().get(APPLICATIONID_INDEX).getValue());
	}

	/**
	 * Initializes a message without parameters and tests if getTuple causes an IllegalArgumentException,
	 * since both name and applicationID are null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest0paramInit() {
		message = new GlobalApplicationMessage();
		message.getTuple();
	}

	/**
	 * Tests whether getTemplate() returns a template matching to the tuple.
	 */
	@Test
	public void templateToTupleTest() {
		message = new GlobalApplicationMessage();
		assertTrue(message.getTemplate().matches(tuple));
	}

	/**
	 * Tests what happens if a message is initialized with a template tuple without existing values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void templateInitializationTest() {
		message = new GlobalApplicationMessage();
		ITuple tuple2 = message.getTemplate();
		message = new GlobalApplicationMessage(tuple2);
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new GlobalApplicationMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}

}
