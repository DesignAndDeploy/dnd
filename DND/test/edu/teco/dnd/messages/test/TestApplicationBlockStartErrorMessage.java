package edu.teco.dnd.messages.test;

import static edu.teco.dnd.messages.ApplicationBlockStartErrorMessage.BLOCKID_INDEX;
import static edu.teco.dnd.messages.ApplicationBlockStartErrorMessage.MESSAGE_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;

import edu.teco.dnd.messages.ApplicationBlockStartErrorMessage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the ApplicationBlockStartErrorMessage.
 */
public class TestApplicationBlockStartErrorMessage {

	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "BlockStartError";

	/**
	 * The ID of the block that failed to start.
	 */
	private String blockID;

	/**
	 * The error message.
	 */
	private String text;

	/**
	 * The cause, if applicable.
	 */
	private Throwable cause;

	/**
	 * uid used for testing.
	 */
	private Long uid;

	/**
	 * message used for testing.
	 * */
	private ApplicationBlockStartErrorMessage message;

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
		blockID = "1337";
		text = "hello";
		cause = new Throwable("arg");
		uid = UUID.randomUUID().getLeastSignificantBits();

		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(blockID);
		tuple.addActual(text);
		tuple.addActual(cause);
		tuple.addActual(uid);
	}

	/**
	 * Initializes the message with parameters and tests whether text was set correctly.
	 */
	@Test
	public void testText3param() {
		message = new ApplicationBlockStartErrorMessage(blockID, text, cause);
		assertEquals(text, message.getMessage());
	}

	/**
	 * Initializes the message with parameters and tests whether cause was set correctly.
	 */
	@Test
	public void testCause3param() {
		message = new ApplicationBlockStartErrorMessage(blockID, text, cause);
		assertEquals(cause, message.getCause());
	}

	/**
	 * Initializes the message with parameters and tests whether blockID was set correctly.
	 */
	@Test
	public void testBlockID3param() {
		message = new ApplicationBlockStartErrorMessage(blockID, text, cause);
		assertEquals(blockID, message.getBlockID());
	}

	/**
	 * Initializes the message without parameters and tests whether parameters are null.
	 */
	@Test
	public void test0param() {
		message = new ApplicationBlockStartErrorMessage();
		assertNull(message.getBlockID());
		assertNull(message.getMessage());
		assertNull(message.getCause());
	}

	/**
	 * Initializes a message with parameters and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTest3paramInit() {
		message = new ApplicationBlockStartErrorMessage(blockID, text, cause);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(blockID, message.getTuple().get(BLOCKID_INDEX).getValue());
		assertEquals(text, message.getTuple().get(MESSAGE_INDEX).getValue());
	}

	/**
	 * Initializes a message without parameters and tests if getTuple causes an IllegalArgumentException,
	 * since both agentID and blockID are null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest0paramInit() {
		message = new ApplicationBlockStartErrorMessage();
		message.getTuple();
	}

	/**
	 * Tests what happens if a message is initialized with a template tuple without existing values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void templateInitializationTest() {
		message = new ApplicationBlockStartErrorMessage();
		ITuple tuple2 = message.getTemplate();
		message = new ApplicationBlockStartErrorMessage(tuple2);
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new ApplicationBlockStartErrorMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}

}
