package edu.teco.dnd.messages;

import java.io.Serializable;
import java.util.UUID;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;

/**
 * This message is used to signal that a block could not be started.
 */
public class ApplicationBlockStartErrorMessage implements Message {

	/**
	 * This class is created to hold a value of the class Throwable or of any class that extends Throwable.
	 */
	private static final class ValueHolder implements Serializable {
		/**
		 * Used for serialization.
		 */
		private static final long serialVersionUID = -6385471528585123502L;

		/**
		 * Value to be held by the ValueHolder.
		 */
		private final Throwable value;

		/**
		 * Initializes a new ValueHolder.
		 * 
		 * @param value
		 *            Value the ValueHolder shall hold
		 */
		public ValueHolder(final Throwable value) {
			this.value = value;
		}

		/**
		 * Returns the value that the ValueHolder is holding.
		 * 
		 * @return value the ValueHolder is holding.
		 */
		public Throwable getValue() {
			return value;
		}
	}

	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "BlockStartError";

	/**
	 * Index of the blockID in the tuple.
	 */
	public static final int BLOCKID_INDEX = 1;
	/**
	 * Index of the Message in the tuple.
	 */
	public static final int MESSAGE_INDEX = 2;

	/**
	 * Index of the cause in the tuple.
	 */
	public static final int CAUSE_INDEX = 3;
	/**
	 * Index of the uid in the tuple.
	 */
	public static final int UID_INDEX = 4;

	/**
	 * Returns a template tuple for this kind of message.
	 * 
	 * @return a template tuple for this kind of message
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
	 * The ID of the block that failed to start.
	 */
	private String blockID;

	/**
	 * The error message.
	 */
	private String message;

	/**
	 * The cause, if applicable.
	 */
	private ValueHolder cause;

	/**
	 * UID used for managing the message, as lights is buggy and won't properly retrive tuples containing
	 * ValueHolders.
	 */
	private Long uid;

	/**
	 * Initializes a new ApplicationBlockStartErrorMessage.
	 * 
	 * @param blockID
	 *            the ID of the block that failed to start
	 * @param message
	 *            an error message
	 * @param cause
	 *            the Throwable that caused the error, if applicable
	 */
	public ApplicationBlockStartErrorMessage(final String blockID, final String message, final Throwable cause) {
		this.blockID = blockID;
		this.message = message;
		this.cause = new ValueHolder(cause);
		uid = UUID.randomUUID().getLeastSignificantBits();
	}

	/**
	 * Initializes a new AppilcationBlockStartErrorMessage from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * 
	 * @see #getTemplate()
	 */
	public ApplicationBlockStartErrorMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new ApplicationBlockStartErrorMessage without parameters.
	 */
	public ApplicationBlockStartErrorMessage() {
		this(null, null, null);
	}

	/**
	 * Returns the ID of the block that failed to start.
	 * 
	 * @return the ID of the block that failed to start
	 */
	public String getBlockID() {
		return blockID;
	}

	/**
	 * Sets the ID of the block that failed to start.
	 * 
	 * @param blockID
	 *            the ID of the block that failed to start
	 */
	public void setBlockID(final String blockID) {
		this.blockID = blockID;
	}

	/**
	 * Returns the error message.
	 * 
	 * @return the error message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the error message.
	 * 
	 * @param message
	 *            the error message
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * Returns the cause of the error.
	 * 
	 * @return the cause of the error
	 */
	public Throwable getCause() {
		return cause.getValue();
	}

	/**
	 * Sets the cause of the error.
	 * 
	 * @param cause
	 *            the cause of the error
	 */
	public void setCause(final Throwable cause) {
		this.cause = new ValueHolder(cause);
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
		tuple.addActual(this.blockID);
		tuple.addActual(this.message);
		tuple.addActual(this.cause);
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
		tuple.addActual(this.blockID);
		tuple.addActual(this.message);
		tuple.addFormal(ValueHolder.class);
		tuple.addActual(uid);
		return tuple;
	}

	/**
	 * Sets the data of this message from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from
	 * @see #getTemplate()
	 */
	@Override
	public void setTuple(final ITuple tuple) {
		if (checkTuple(tuple)) {
			throw new IllegalArgumentException("invalid tuple");
		}
		IField[] fields = tuple.getFields();
		this.blockID = (String) fields[BLOCKID_INDEX].getValue();
		this.message = (String) fields[MESSAGE_INDEX].getValue();
		this.cause = (ValueHolder) fields[CAUSE_INDEX].getValue();
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
