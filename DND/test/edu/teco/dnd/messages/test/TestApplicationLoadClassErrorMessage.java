package edu.teco.dnd.messages.test;

import static edu.teco.dnd.messages.ApplicationLoadClassErrorMessage.CLASSNAME_INDEX;
import static edu.teco.dnd.messages.ApplicationLoadClassErrorMessage.MESSAGE_INDEX;
import static edu.teco.dnd.messages.ApplicationLoadClassErrorMessage.MODULEID_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;

import edu.teco.dnd.messages.ApplicationLoadClassErrorMessage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the ApplicationLoadClassErrorMessage.
 */
public class TestApplicationLoadClassErrorMessage {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "LoadClassError";

	/**
	 * The name of the class that failed to load.
	 */
	private String className;

	/**
	 * The error message.
	 */
	private String text;

	/**
	 * The cause, if applicable.
	 */
	private Throwable cause;

	/**
	 * The moduleID.
	 */
	private Long moduleID;

	/**
	 * uid, used for testing.
	 */
	private Long uid;

	/**
	 * message used for testing.
	 */
	private ApplicationLoadClassErrorMessage message;

	/**
	 * tuple used for testing and initializing message2.
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
		className = "name";
		text = "hello";
		cause = new Throwable("arg");
		moduleID = 0L;
		uid = UUID.randomUUID().getLeastSignificantBits();

		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(className);
		tuple.addActual(text);
		tuple.addActual(cause);
		tuple.addActual(moduleID);
		tuple.addActual(uid);
	}

	/**
	 * Initializes the message with parameters and tests whether className was set correctly.
	 */
	@Test
	public void testclassName4param() {
		message = new ApplicationLoadClassErrorMessage(className, text, cause, moduleID);
		assertEquals(className, message.getClassName());
	}

	/**
	 * Initializes the message with parameters and tests whether the error message (text) was set correctly.
	 */
	@Test
	public void testMessage4param() {
		message = new ApplicationLoadClassErrorMessage(className, text, cause, moduleID);
		assertEquals(text, message.getMessage());
	}

	/**
	 * Initializes the message with parameters and tests whether the cause was set correctly.
	 */
	@Test
	public void testCauseID4param() {
		message = new ApplicationLoadClassErrorMessage(className, text, cause, moduleID);
		assertEquals(cause, message.getCause());
	}

	/**
	 * Initializes the message with parameters and tests whether moduleID was set correctly.
	 */
	@Test
	public void testModuleID4param() {
		message = new ApplicationLoadClassErrorMessage(className, text, cause, moduleID);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes the message without parameters and tests whether parameters are null.
	 */
	@Test
	public void testclassName0param() {
		message = new ApplicationLoadClassErrorMessage();
		assertNull(message.getModuleID());
		assertNull(message.getClassName());
		assertNull(message.getCause());
		assertNull(message.getModuleID());
	}

	/**
	 * Initializes a message with 4 parameters and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTest2paramInit() {
		message = new ApplicationLoadClassErrorMessage(className, text, cause, moduleID);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(className, message.getTuple().get(CLASSNAME_INDEX).getValue());
		assertEquals(text, message.getTuple().get(MESSAGE_INDEX).getValue());
		assertEquals(moduleID, message.getTuple().get(MODULEID_INDEX).getValue());
	}

	/**
	 * Initializes a message without parameters and tests if getTuple causes an IllegalArgumentException,
	 * since both className and moduleID are null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest0paramInit() {
		message = new ApplicationLoadClassErrorMessage();
		message.getTuple();
	}

	/**
	 * Tests what happens if a message is initialized with a template tuple without existing values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void templateInitializationTest() {
		message = new ApplicationLoadClassErrorMessage();
		ITuple tuple2 = message.getTemplate();
		message = new ApplicationLoadClassErrorMessage(tuple2);
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new ApplicationLoadClassErrorMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}

}
