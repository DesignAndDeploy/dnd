package edu.teco.dnd.messages;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;

/**
 * This message is used to signal that a class has been loaded successfully.
 */
public class ApplicationClassLoadedMessage implements Message {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "ClassLoaded";

	/**
	 * Index of the moduleID in the tuple.
	 */
	public static final int MODULEID_INDEX = 1;
	/**
	 * Index of the className in the tuple.
	 */
	public static final int CLASSNAME_INDEX = 2;

	/**
	 * Returns a template tuple for this kind of message.
	 * 
	 * @return a template tuple for this kind of message
	 */
	@Override
	public ITuple getTemplate() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addFormal(Long.class);
		tuple.addFormal(String.class);
		return tuple;
	}

	/**
	 * The ID of the module that generated the message.
	 */
	private Long moduleID;

	/**
	 * The name of the class that has been loaded.
	 */
	private String className;

	/**
	 * Initializes a new ApplicationClassLoadedMessage.
	 * 
	 * @param moduleID
	 *            the ID of the module that generated the message
	 * @param className
	 *            the name of the class that has been loaded
	 */
	public ApplicationClassLoadedMessage(final Long moduleID, final String className) {
		this.moduleID = moduleID;
		this.className = className;
	}

	/**
	 * Initializes a new AppilcationClassLoadedMessage from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * 
	 * @see #getTemplate()
	 */
	public ApplicationClassLoadedMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new ApplicationClassLoadedMessage without parameters.
	 */
	public ApplicationClassLoadedMessage() {
		this(null, null);
	}

	/**
	 * Returns the ID of the module.
	 * 
	 * @return the ID of the module
	 */
	public Long getModuleID() {
		return moduleID;
	}

	/**
	 * Sets the ID of the module.
	 * 
	 * @param moduleID
	 *            the ID of the module
	 */
	public void setModuleID(final Long moduleID) {
		this.moduleID = moduleID;
	}

	/**
	 * Returns the class name.
	 * 
	 * @return the class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the class name.
	 * 
	 * @param className
	 *            the class name
	 */
	public void setClassName(final String className) {
		this.className = className;
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
		tuple.addActual(this.moduleID);
		tuple.addActual(this.className);
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
		IField[] field = tuple.getFields();
		moduleID = (Long) field[MODULEID_INDEX].getValue();
		className = (String) field[CLASSNAME_INDEX].getValue();
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
