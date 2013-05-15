package edu.teco.dnd.messages;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;

/**
 * This message is used to tell a module that it should load a class.
 */
public class ApplicationLoadClassMessage implements Message {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "LoadClass";

	/**
	 * Index of the classNames in the tuple.
	 */
	public static final int CLASSNAMES_INDEX = 1;

	/**
	 * Index of the muServer in the tuple.
	 */
	public static final int MUSERVER_INDEX = 2;

	/**
	 * Index of the mainClassID in the tuple.
	 */
	public static final int MAINCLASS_INDEX = 3;

	/**
	 * Index of the uid in the tuple.
	 */
	public static final int UID_INDEX = 4;

	/**
	 * This class is created to hold a value of the class Serializable or of any class that extends
	 * Serializable.
	 */
	private static final class ValueHolder implements Serializable {
		/**
		 * Used for serialization.
		 */
		private static final long serialVersionUID = -6384471528585920502L;

		/**
		 * Value to be held by the ValueHolder.
		 */
		private final Set<String> value;

		/**
		 * Initializes a new ValueHolder.
		 * 
		 * @param value
		 *            Value the ValueHolder shall hold
		 */
		public ValueHolder(final Set<String> value) {
			this.value = value;
		}

		/**
		 * Returns the value that the ValueHolder is holdung.
		 * 
		 * @return value the ValueHolder is holding.
		 */
		public Set<String> getValue() {
			return value;
		}
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
		tuple.addFormal(ValueHolder.class);
		tuple.addFormal(String.class);
		tuple.addFormal(String.class);
		tuple.addFormal(Long.class);
		return tuple;

	}

	/**
	 * The names of the classes to load. These classes are needed for the use of the functionBlock that shall
	 * be loaded.
	 */
	private ValueHolder classNames = new ValueHolder(new HashSet<String>());

	/**
	 * The address of the muServer.
	 */
	private String muServer;

	/**
	 * The name of the class of the functionBlock to load.
	 */
	private String mainClass;

	/**
	 * UID used for managing the message, as lights is buggy and won't properly retrieve tuples containing
	 * ValueHolders.
	 */
	private Long uid;

	/**
	 * Initializes a new ApplicationLoadClassMessage.
	 * 
	 * @param classNames
	 *            the names of the classes to load
	 * @param muServer
	 *            the address of the muServer to load from
	 * @param mainClass
	 *            name of the main class
	 */
	public ApplicationLoadClassMessage(final Collection<String> classNames, final String muServer,
			final String mainClass) {
		if (classNames != null) {
			this.classNames.getValue().addAll(classNames);
		}
		this.muServer = muServer;
		this.mainClass = mainClass;
		uid = UUID.randomUUID().getLeastSignificantBits();
	}

	/**
	 * Initializes a new AppilcationStartBlockMessage from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * 
	 * @see #getTemplate()
	 */
	public ApplicationLoadClassMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new ApplicationLoadClassMessage without parameters.
	 */
	public ApplicationLoadClassMessage() {
		this(null, null, null);
	}

	/**
	 * Returns a set containing the names of the classes that should be loaded.
	 * 
	 * @return the names of the classes to load
	 */
	public Set<String> getClassNames() {
		return classNames.getValue();
	}

	/**
	 * Sets the names of the classes that should be loaded.
	 * 
	 * @param classNames
	 *            names of the classes to be loaded
	 */
	public void setClassNames(final Collection<String> classNames) {
		this.classNames.getValue().clear();
		if (classNames != null) {
			this.classNames.getValue().addAll(classNames);
		}
	}

	/**
	 * Returns the address of the MuServer that should be used.
	 * 
	 * @return the address of the MuServer to use
	 */
	public String getMuServer() {
		return muServer;
	}

	/**
	 * Sets the address of the MuServer that should be used.
	 * 
	 * @param muServer
	 *            Address of the MuServer that should be used
	 */
	public void setMuServer(final String muServer) {
		this.muServer = muServer;
	}

	/**
	 * Returns the name of the main class.
	 * 
	 * @return the name of the main class
	 */
	public String getMainClass() {
		return mainClass;
	}

	/**
	 * Sets the name of the main class.
	 * 
	 * @param mainClass
	 *            name of the main class
	 */
	public void setMainClass(final String mainClass) {
		this.mainClass = mainClass;
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
		tuple.addActual(classNames);
		tuple.addActual(muServer);
		tuple.addActual(mainClass);
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
		tuple.addFormal(ValueHolder.class);
		tuple.addActual(muServer);
		tuple.addActual(mainClass);
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
		classNames = (ValueHolder) fields[CLASSNAMES_INDEX].getValue();
		muServer = (String) fields[MUSERVER_INDEX].getValue();
		mainClass = (String) fields[MAINCLASS_INDEX].getValue();
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
