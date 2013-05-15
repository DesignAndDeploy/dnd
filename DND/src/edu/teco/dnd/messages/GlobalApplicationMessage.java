package edu.teco.dnd.messages;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;

/**
 * This messages is used to make public that there is an application with a name and an ID.
 */

public class GlobalApplicationMessage implements Message {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Application";

	/**
	 * Index of the name in the tuple.
	 */
	public static final int NAME_INDEX = 1;

	/**
	 * Index of the applicationID in the tuple.
	 */
	public static final int APPLICATIONID_INDEX = 2;

	/**
	 * This attribute contains the ID of an application.
	 */
	private Integer applicationID;

	/**
	 * This attribute contains the name of an application.
	 */
	private String name;

	/**
	 * Initializes a new GlobalApplicationMessage.
	 * 
	 * @param name
	 *            Name of the application
	 * @param applicationID
	 *            ID of the application
	 */
	public GlobalApplicationMessage(final String name, final Integer applicationID) {
		this.name = name;
		this.applicationID = applicationID;
	}

	/**
	 * Initializes a new GlobalApplicationMessage from a tuple.
	 * 
	 * @param tuple
	 *            tuple to get data from
	 * 
	 * @see #getTemplate()
	 */
	public GlobalApplicationMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new GlobalApplicationMessage without parameters.
	 */
	public GlobalApplicationMessage() {
		this(null, null);
	}

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
		tuple.addFormal(Integer.class);
		return tuple;
	}

	/**
	 * Returns a tuple representing this message.
	 * 
	 * @return a tuple representing this message
	 */
	public ITuple getTuple() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(name);
		tuple.addActual(applicationID);
		return tuple;
	}

	/**
	 * Returns ID of application.
	 * 
	 * @return ID of application
	 */
	public Integer getApplicationID() {
		return applicationID;
	}

	/**
	 * sets ID of application.
	 * 
	 * @param applicationID
	 *            ID of application
	 */
	public void setApplicationID(final Integer applicationID) {
		this.applicationID = applicationID;
	}

	/**
	 * Returns the name of an application.
	 * 
	 * @return name of application
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name of application.
	 * 
	 * @param name
	 *            new name for application
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Sets the data of this message from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * @see #getTemplate()
	 */
	public void setTuple(final ITuple tuple) {
		if (checkTuple(tuple)) {
			throw new IllegalArgumentException("invalid tuple");
		}
		IField[] fields = tuple.getFields();
		name = (String) fields[NAME_INDEX].getValue();
		applicationID = (Integer) fields[APPLICATIONID_INDEX].getValue();
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
