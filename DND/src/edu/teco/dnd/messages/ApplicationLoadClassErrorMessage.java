package edu.teco.dnd.messages;

import java.io.Serializable;
import java.util.UUID;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;

/**
 * This message is used to signal that a class could not be loaded.
 */
public class ApplicationLoadClassErrorMessage implements Message {

	/**
	 * This class is created to hold a value of the class Serializable or of any class that extends
	 * Serializable.
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
	public static final String MESSAGE_IDENTIFIER = "LoadClassError";

	/**
	 * Index of the className in the tuple.
	 */
	public static final int CLASSNAME_INDEX = 1;
	/**
	 * Index of the message in the tuple.
	 */
	public static final int MESSAGE_INDEX = 2;

	/**
	 * Index of the cause in the tuple.
	 */
	public static final int CAUSE_INDEX = 3;

	/**
	 * The index of the moduleID in the tuple.
	 */
	public static final int MODULEID_INDEX = 4;
	/**
	 * Index of the uid in the tuple.
	 */
	public static final int UID_INDEX = 5;

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
		tuple.addFormal(Long.class);
		return tuple;

	}

	/**
	 * The name of the class that failed to load.
	 */
	private String className;

	/**
	 * The error message.
	 */
	private String message;

	/**
	 * The cause, if applicable.
	 */
	private ValueHolder cause;

	/**
	 * The module id.
	 */
	private Long moduleID;

	/**
	 * UID used for managing the message, as lights is buggy and won't properly retrive tuples containing
	 * ValueHolders.
	 */
	private Long uid;

	/**
	 * Initializes a new ApplicationLoadClassErrorMessage.
	 * 
	 * @param className
	 *            the name of the class that failed to load
	 * @param message
	 *            an error message
	 * @param cause
	 *            the Throwable that caused the error, if applicable
	 * @param moduleID
	 *            the ID of the module the error occurred on
	 */
	public ApplicationLoadClassErrorMessage(final String className, final String message,
			final Throwable cause, final Long moduleID) {
		this.className = className;
		this.message = message;
		this.cause = new ValueHolder(cause);
		this.moduleID = moduleID;
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
	public ApplicationLoadClassErrorMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new ApplicationBlockStartErrorMessage without parameters.
	 */
	public ApplicationLoadClassErrorMessage() {
		this(null, null, null, null);
	}

	/**
	 * The name of the class that failed to start.
	 * 
	 * @return the name of the class that failed to start
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the name of the class that failed to start.
	 * 
	 * @param className
	 *            the name of the class that failed to start
	 */
	public void setClassName(final String className) {
		this.className = className;
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
	 * Returns the ID of the module the error occurred on.
	 * 
	 * @return the ID of the module the error occurred on
	 */
	public Long getModuleID() {
		return moduleID;
	}

	/**
	 * Sets the module ID.
	 * 
	 * @param moduleID
	 *            the module ID to set
	 */
	public void setModuleID(final long moduleID) {
		this.moduleID = moduleID;
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
		tuple.addActual(className);
		tuple.addActual(message);
		tuple.addActual(cause);
		tuple.addActual(moduleID);
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
		tuple.addActual(className);
		tuple.addActual(message);
		tuple.addFormal(ValueHolder.class);
		tuple.addActual(moduleID);
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
		className = (String) fields[CLASSNAME_INDEX].getValue();
		message = (String) fields[MESSAGE_INDEX].getValue();
		cause = (ValueHolder) fields[CAUSE_INDEX].getValue();
		moduleID = (Long) fields[MODULEID_INDEX].getValue();
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
