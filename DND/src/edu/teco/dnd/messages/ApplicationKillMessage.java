package edu.teco.dnd.messages;

import lights.adapters.Tuple;
import lights.interfaces.ITuple;

/**
 * This class represents a message used to kill an application.
 */
public class ApplicationKillMessage implements Message {

	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Kill";

	/**
	 * Returns a template tuple for this kind of message.
	 * 
	 * @return a template tuple for this kind of message
	 */
	@Override
	public ITuple getTemplate() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		return tuple;
	}

	/**
	 * Returns a tuple representing this message.
	 * 
	 * @return a tuple representing this message
	 */
	@Override
	public ITuple getTuple() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		return tuple;
	}

	/**
	 * Sets the data of this message from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * @see #getTemplate()
	 */
	@Override
	public void setTuple(final ITuple tuple) {
		if (!getTemplate().matches(tuple)) {
			throw new IllegalArgumentException("invalid tuple");
		}
	}

	/**
	 * Initializes a new ApplicationKillMessage. Since an ApplicationKillMessage only consists of it's message
	 * identifier, no arguments are required
	 */
	public ApplicationKillMessage() {

	}

	/**
	 * Initializes a new ApplicationKillMessage from an ITuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * 
	 * @see #getTemplate()
	 */
	public ApplicationKillMessage(final ITuple tuple) {
		setTuple(tuple);
	}

}
