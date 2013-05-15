package edu.teco.dnd.messages;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;
import lime.AgentID;

import edu.teco.dnd.module.ModuleConfig;

/**
 * This message is used for a tuple space of an application. It represents a module that runs (parts of) the
 * application
 * 
 */
public class ApplicationModuleMessage implements Message {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Module";

	/**
	 * Index of the moduleID in the tuple.
	 */
	public static final int MODULEID_INDEX = 1;
	/**
	 * Index of the agentID in the tuple.
	 */
	public static final int AGENTID_INDEX = 2;

	/**
	 * Index of the moduleConfig in the tuple.
	 */
	public static final int MODULECONFIG_INDEX = 3;

	/**
	 * Returns a template tuple for this kind of message.
	 * 
	 * @return a template tuple for this kind of message
	 */
	public ITuple getTemplate() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addFormal(Long.class);
		tuple.addFormal(AgentID.class);
		tuple.addFormal(ModuleConfig.class);
		return tuple;

	}

	/**
	 * Contains the ID of the module.
	 */
	private Long moduleID;

	/**
	 * ID of the ApplicationAgent.
	 */
	private AgentID agentID;

	/**
	 * The ModuleConfig used to describe the module.
	 */
	private ModuleConfig moduleConfig;

	/**
	 * Initializes a new ApplicationModuleMessage.
	 * 
	 * @param moduleID
	 *            ID of the module within the appilcation
	 * @param agentID
	 *            ID of the ApplicationAgent that entered the tuple space
	 * @param moduleConfig
	 *            ModuleConfig describing the module
	 */
	public ApplicationModuleMessage(final Long moduleID, final AgentID agentID,
			final ModuleConfig moduleConfig) {
		this.moduleID = moduleID;
		this.agentID = agentID;
		this.moduleConfig = moduleConfig;
	}

	/**
	 * Initializes a new ApplicationModuleMessage from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * @see #getTemplate()
	 */
	public ApplicationModuleMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new ApplicationModuleMessage without parameters.
	 */
	public ApplicationModuleMessage() {
		this(null, null, null);
	}

	/**
	 * Returns ID of the module.
	 * 
	 * @return ID of the module
	 */
	public Long getModuleID() {
		return moduleID;
	}

	/**
	 * Sets ID of the module.
	 * 
	 * @param moduleID
	 *            ID of the module
	 */
	public void setModuleID(final Long moduleID) {
		this.moduleID = moduleID;
	}

	/**
	 * Returns ID of the ApplicationAgent.
	 * 
	 * @return ID of the ApplicationAgent
	 */
	public AgentID getAgentID() {
		return agentID;
	}

	/**
	 * Sets ID of the ApplicationAgent.
	 * 
	 * @param agentID
	 *            ID of the ApplicationAgent
	 */
	public void setAgentID(final AgentID agentID) {
		this.agentID = agentID;
	}

	/**
	 * Returns the ModuleConfig used in this message.
	 * 
	 * @return the ModuleConfig used in this message
	 */
	public ModuleConfig getModuleConfig() {
		return moduleConfig;
	}

	/**
	 * Sets the ModuleConfig to use in this message.
	 * 
	 * @param moduleConfig
	 *            the ModuleConfig to use in this message
	 */
	public void setModuleConfig(final ModuleConfig moduleConfig) {
		this.moduleConfig = moduleConfig;
	}

	/**
	 * Returns a tuple representing this message.
	 * 
	 * @return a tuple representing this message
	 */
	public ITuple getTuple() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(moduleID);
		tuple.addActual(agentID);
		tuple.addActual(moduleConfig); // moduleConfig not serializable
		return tuple;
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
		moduleID = (Long) fields[MODULEID_INDEX].getValue();
		agentID = (AgentID) fields[AGENTID_INDEX].getValue();
		moduleConfig = (ModuleConfig) fields[MODULECONFIG_INDEX].getValue();
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
