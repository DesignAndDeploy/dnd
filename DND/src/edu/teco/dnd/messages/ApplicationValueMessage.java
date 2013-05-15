package edu.teco.dnd.messages;

import java.io.Serializable;
import java.util.UUID;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;

/**
 * This message is used to sent a new value to a certain input of a functionBlock.
 */
public class ApplicationValueMessage implements Message {

	/**
	 * This class is created to hold a value of the class Serializable or of any class that extends
	 * Serializable.
	 */
	private static final class ValueHolder implements Serializable {
		/**
		 * Used for serialization.
		 */
		private static final long serialVersionUID = -6385471528585920502L;

		/**
		 * Value to be held by the ValueHolder.
		 */
		private final Serializable value;

		/**
		 * Initializes a new ValueHolder.
		 * 
		 * @param value
		 *            Value the ValueHolder shall hold
		 */
		public ValueHolder(final Serializable value) {
			this.value = value;
		}

		/**
		 * Returns the value that the ValueHolder is holdung.
		 * 
		 * @return value the ValueHolder is holding.
		 */
		public Serializable getValue() {
			return value;
		}
	}

	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Value";

	/**
	 * Index of the functionBlockID in the tuple.
	 */
	public static final int FUNCTIONBLOCKID_INDEX = 1;
	/**
	 * Index of the input in the tuple.
	 */
	public static final int INPUT_INDEX = 2;

	/**
	 * Index of the value in the tuple.
	 */
	public static final int VALUE_INDEX = 3;

	/**
	 * Index of the uid in the tuple.
	 */
	public static final int UID_INDEX = 4;

	/**
	 * Returns a template tuple that matches this kind of Message. Although this should mostly return static
	 * data this is not a static method as interfaces can only declare non static methods.
	 * 
	 * @return a tuple that acts as a template for this kind of message
	 */
	@Override
	public ITuple getTemplate() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addFormal(String.class);
		tuple.addFormal(String.class);
		tuple.addFormal(ValueHolder.class);
		tuple.addFormal(Long.class);
		return tuple;

	}

	/**
	 * ID of functionBlock whose Input is to be updated.
	 */
	private String functionBlockID;

	/**
	 * String describing which input of the functionBlock receives a new value.
	 */
	private String input;

	/**
	 * Value to be received by the input of the function block.
	 */
	private ValueHolder value;

	/**
	 * UID used for managing the message, as lights is buggy and won't properly retrive tuples containing
	 * ValueHolders.
	 */
	private Long uid;

	/**
	 * Initializes a new ApplicationValueMessage.
	 * 
	 * @param functionBlockID
	 *            ID of the functionBlock which gets a new value
	 * @param input
	 *            input of said functionBlock to receive value
	 * @param value
	 *            New value for the input of the funcitonBlock
	 */
	public ApplicationValueMessage(final String functionBlockID, final String input, final Serializable value) {
		this.functionBlockID = functionBlockID;
		this.input = input;
		this.value = new ValueHolder(value);
		uid = UUID.randomUUID().getLeastSignificantBits();
	}

	/**
	 * Initializes a new ApplicationValueMessage from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * @see #getTemplate()
	 */
	public ApplicationValueMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new ApplicationValueMessage without parameters.
	 */
	public ApplicationValueMessage() {
		this(null, null, null);
	}

	/**
	 * Returns ID of the functionBlock.
	 * 
	 * @return ID of the functionBlock
	 */
	public String getFunctionBlockID() {
		return functionBlockID;
	}

	/**
	 * Sets ID of the functionBlock.
	 * 
	 * @param functionBlockID
	 *            ID of the functionBlock
	 */
	public void setFunctionBlockID(final String functionBlockID) {
		this.functionBlockID = functionBlockID;
	}

	/**
	 * Returns the input of the functionBlock that gets a new value.
	 * 
	 * @return input of the functionBlock
	 */
	public String getInput() {
		return input;
	}

	/**
	 * Specifies the input of the functionBlock which receives a new value.
	 * 
	 * @param input
	 *            input of functionBlock to receive new value
	 */
	public void setInput(final String input) {
		this.input = input;
	}

	/**
	 * Returns value the functionBlock shall receive.
	 * 
	 * @return value which the functionBlock shall receive
	 */
	public Serializable getValue() {
		return value.getValue();
	}

	/**
	 * Sets the value that shall be sent to the functionBlock.
	 * 
	 * @param value
	 *            new value to sent
	 */
	public void setValue(final Serializable value) {
		this.value = new ValueHolder(value);
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
		tuple.addActual(functionBlockID);
		tuple.addActual(input);
		tuple.addActual(value);
		tuple.addActual(uid);
		return tuple;
	}

	/**
	 * Used to get a template to match this tuple in tuplespace. Necessary to work around a ligths bug.
	 * 
	 * @return the matcher for this tuple.
	 */
	public ITuple getUidMatcherTuple() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(functionBlockID);
		tuple.addActual(input);
		tuple.addFormal(ValueHolder.class); // won't match otherwise.
		tuple.addActual(uid);
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
		if (checkTuple(tuple)) {
			throw new IllegalArgumentException("invalid tuple");
		}
		IField[] fields = tuple.getFields();
		functionBlockID = (String) fields[FUNCTIONBLOCKID_INDEX].getValue();
		input = (String) fields[INPUT_INDEX].getValue();
		value = ((ValueHolder) fields[VALUE_INDEX].getValue());
		uid = (Long) fields[UID_INDEX].getValue();
	}

	/**
	 * Checks whether a tuple matches the required template and has valid fields.
	 * 
	 * @param tuple
	 *            Tuple to check
	 * @return false if tuple can be used, true if there are any problems
	 */
	private boolean checkTuple(final ITuple tuple) {
		if (!getTemplate().matches(tuple)) {
			return true;
		} else {
			IField[] fields = tuple.getFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].isFormal()) {
					return true;
				}
			}
		}
		return false;
	}
}
