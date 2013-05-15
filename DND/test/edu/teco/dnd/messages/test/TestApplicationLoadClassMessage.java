package edu.teco.dnd.messages.test;

import static edu.teco.dnd.messages.ApplicationLoadClassMessage.MAINCLASS_INDEX;
import static edu.teco.dnd.messages.ApplicationLoadClassMessage.MUSERVER_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;

import edu.teco.dnd.messages.ApplicationLoadClassMessage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the ApplicationLoadClassMessage.
 */
public class TestApplicationLoadClassMessage {

	/**
	 * This is used as the first field in the tuple to identify this type of muServer.
	 */
	public static final String MESSAGE_IDENTIFIER = "LoadClass";

	/**
	 * Specific name of a class.
	 */
	private String name;

	/**
	 * Names of the classes to load.
	 */
	private Collection<String> classNames;

	/**
	 * The address of the muServer.
	 */
	private String muServer;

	/**
	 * The name of the class of the functionBlock to load.
	 */
	private String mainClass;

	/**
	 * UID used for testing.
	 */
	private Long uid;

	/**
	 * message used for testing.
	 */
	private ApplicationLoadClassMessage message;

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
		classNames = new ArrayList<String>();
		String name = "blubb";
		classNames.add(name);
		muServer = "hello";
		mainClass = "main";
		uid = UUID.randomUUID().getLeastSignificantBits();

		tuple = new Tuple();
	}

	/**
	 * Initializes the message with parameters and tests whether classNames were set correctly.
	 */
	@Test
	public void testclassName3param() {
		message = new ApplicationLoadClassMessage(classNames, muServer, mainClass);
		assertTrue(message.getClassNames().containsAll(classNames));
	}

	/**
	 * Initializes the message with parameters and tests whether the muServer was set correctly.
	 */
	@Test
	public void testMessage3param() {
		message = new ApplicationLoadClassMessage(classNames, muServer, mainClass);
		assertEquals(muServer, message.getMuServer());
	}

	/**
	 * Initializes the message with parameters and tests whether the mainClass was set correctly.
	 */
	@Test
	public void testCauseID3param() {
		message = new ApplicationLoadClassMessage(classNames, muServer, mainClass);
		assertEquals(mainClass, message.getMainClass());
	}

	/**
	 * Initializes the message without parameters and tests whether parameters are null.
	 */
	@Test
	public void testclassName0param() {
		message = new ApplicationLoadClassMessage();
		assertTrue(message.getClassNames().isEmpty());
		assertNull(message.getMuServer());
		assertNull(message.getMainClass());
	}

	/**
	 * Initializes a message with 3 parameters and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTest3paramInit() {
		message = new ApplicationLoadClassMessage(classNames, muServer, mainClass);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		// assertEquals(classNames, message.getTuple().get(CLASSNAMES_INDEX).getValue());
		assertEquals(muServer, message.getTuple().get(MUSERVER_INDEX).getValue());
		assertEquals(mainClass, message.getTuple().get(MAINCLASS_INDEX).getValue());
	}

	/**
	 * Initializes a message without parameters and tests if getTuple causes an IllegalArgumentException,
	 * since both className and mainClass are null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest0paramInit() {
		message = new ApplicationLoadClassMessage();
		message.getTuple();
	}

	/**
	 * Tests what happens if a message is initialized with a template tuple without existing values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void templateInitializationTest() {
		message = new ApplicationLoadClassMessage();
		ITuple tuple2 = message.getTemplate();
		message = new ApplicationLoadClassMessage(tuple2);
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new ApplicationLoadClassMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}

}
