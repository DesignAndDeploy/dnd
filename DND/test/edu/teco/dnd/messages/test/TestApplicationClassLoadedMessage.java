package edu.teco.dnd.messages.test;

import static edu.teco.dnd.messages.ApplicationClassLoadedMessage.CLASSNAME_INDEX;
import static edu.teco.dnd.messages.ApplicationClassLoadedMessage.MODULEID_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;

import edu.teco.dnd.messages.ApplicationClassLoadedMessage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the ApplicationClassLoadedMessage.
 */
public class TestApplicationClassLoadedMessage {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "ClassLoaded";

	/**
	 * The ID of the module that generated the message.
	 */
	private Long moduleID;

	/**
	 * The name of the class that has been loaded.
	 */
	private String className;

	/**
	 * message used for testing.
	 */
	private ApplicationClassLoadedMessage message;

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
		moduleID = 0L;
		className = "name";

		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(moduleID);
		tuple.addActual(className);
	}

	/**
	 * Initializes the message with parameters and tests whether moduleID was set correctly.
	 */
	@Test
	public void testmoduleID2param() {
		message = new ApplicationClassLoadedMessage(moduleID, className);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes the message with parameters and tests whether className was set correctly.
	 */
	@Test
	public void testclassName2param() {
		message = new ApplicationClassLoadedMessage(moduleID, className);
		assertEquals(className, message.getClassName());
	}

	/**
	 * Initializes the message without parameters and tests whether parameters are null.
	 */
	@Test
	public void testmoduleID0param() {
		message = new ApplicationClassLoadedMessage();
		assertNull(message.getClassName());
		assertNull(message.getModuleID());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether moduleID was set correctly.
	 */
	@Test
	public void testmoduleIDtuple() {
		message = new ApplicationClassLoadedMessage(tuple);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether className was set correctly.
	 */
	@Test
	public void testclassNametuple() {
		message = new ApplicationClassLoadedMessage(tuple);
		assertEquals(className, message.getClassName());
	}

	/**
	 * Initializes a message with a tuple and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTesttupleInit() {
		message = new ApplicationClassLoadedMessage(tuple);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(moduleID, message.getTuple().get(MODULEID_INDEX).getValue());
		assertEquals(className, message.getTuple().get(CLASSNAME_INDEX).getValue());
	}

	/**
	 * Initializes a message with two parameters and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTest2paramInit() {
		message = new ApplicationClassLoadedMessage(moduleID, className);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(moduleID, message.getTuple().get(MODULEID_INDEX).getValue());
		assertEquals(className, message.getTuple().get(CLASSNAME_INDEX).getValue());
	}

	/**
	 * Initializes a message without parameters and tests if getTuple causes an IllegalArgumentException,
	 * since both moduleID and className are null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest0paramInit() {
		message = new ApplicationClassLoadedMessage();
		message.getTuple();
	}

	/**
	 * Tests whether getTemplate() returns a template matching to the tuple.
	 */
	@Test
	public void templateToTupleTest() {
		message = new ApplicationClassLoadedMessage();
		assertTrue(message.getTemplate().matches(tuple));
	}

	/**
	 * Tests what happens if a message is initialized with a template tuple without existing values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void templateInitializationTest() {
		message = new ApplicationClassLoadedMessage();
		ITuple tuple2 = message.getTemplate();
		message = new ApplicationClassLoadedMessage(tuple2);
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new ApplicationClassLoadedMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}
}
