package edu.teco.dnd.messages;

import lights.interfaces.ITuple;

/**
 * Defines a Message that can be sent or received via tuples.
 */
public interface Message {
	/**
	 * Returns a template tuple that matches this kind of Message. Although this should mostly return static
	 * data this is not a static method as interfaces can only declare non static methods.
	 * 
	 * @return a tuple that acts as a template for this kind of message
	 */
	ITuple getTemplate();

	/**
	 * Returns a tuple representing this message.
	 * 
	 * @return a tuple representing this message
	 */
	ITuple getTuple();

	/**
	 * Sets the data of this message from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from
	 * @see #getTemplate()
	 */
	void setTuple(ITuple tuple);
}
