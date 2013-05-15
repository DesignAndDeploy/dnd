package edu.teco.dnd.messages;

import java.util.UUID;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;
import lime.AgentID;

/**
 * This message is sent to all modules that shall execute certain parts of an application. Within this
 * application, the modules get a seperate ID.
 * 
 */
public class GlobalJoinMessage implements Message {

	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Join";

	/**
	 * Index of the applicationID in the tuple.
	 */
	public static final int APPLICATIONID_INDEX = 1;
	/**
	 * Index of the ID of the DeploymentAgent in the tuple.
	 */
	public static final int DEPLOYID_INDEX = 2;

	/**
	 * Index of the UID in the tuple.
	 */
	public static final int UID_INDEX = 3;

	/**
	 * Returns a template tuple for this kind of message.
	 * 
	 * @return a template tuple for this kind of message
	 */
	@Override
	public ITuple getTemplate() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addFormal(Integer.class);
		tuple.addFormal(AgentID.class);
		tuple.addFormal(Long.class);
		return tuple;
	}

	/**
	 * ID of application to run on a module.
	 */
	private Integer applicationID;

	/**
	 * ID of the deploymentAgent.
	 */
	private AgentID deploymentAgentID;

	/**
	 * UID used for managing the message, as lights is buggy and won't properly retrive tuples containing
	 * ValueHolders.
	 */
	private Long uid;

	/**
	 * Initializes a new GlobalJoinMessage.
	 * 
	 * @param applicationID
	 *            ID of application
	 * @param deploymentAgentID
	 *            AgentID of the deployment Agent (where to send reply).
	 */
	public GlobalJoinMessage(final Integer applicationID, final AgentID deploymentAgentID) {
		this.applicationID = applicationID;
		this.deploymentAgentID = deploymentAgentID;
		uid = UUID.randomUUID().getLeastSignificantBits();
	}

	/**
	 * Initializes a new GlobalModuleMessage from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get data from
	 * 
	 * @see #getTemplate()
	 */
	public GlobalJoinMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new GlobalJoinMessage without parameters.
	 */
	public GlobalJoinMessage() {
		this(null, null);
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
	 * Sets ID of application.
	 * 
	 * @param applicationID
	 *            new ID of application
	 */
	public void setApplicationID(final Integer applicationID) {
		this.applicationID = applicationID;
	}

	/**
	 * Returns ID of the DeploymentAgent within the application.
	 * 
	 * @return ID of DeploymentAgent within the application
	 */
	public AgentID getDeploymentAgentID() {
		return deploymentAgentID;
	}

	/**
	 * Sets the ID the DeploymentAgent within the application.
	 * 
	 * @param deploymentAgentID
	 *            ID of the DeploymentAgent within the application
	 */
	public void setDeploymentAgentID(final AgentID deploymentAgentID) {
		this.deploymentAgentID = deploymentAgentID;
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
		tuple.addActual(this.applicationID);
		tuple.addActual(this.deploymentAgentID);
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
		tuple.addActual(this.applicationID);
		tuple.addFormal(AgentID.class);
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
		this.applicationID = (Integer) fields[APPLICATIONID_INDEX].getValue();
		this.deploymentAgentID = (AgentID) fields[DEPLOYID_INDEX].getValue();
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
