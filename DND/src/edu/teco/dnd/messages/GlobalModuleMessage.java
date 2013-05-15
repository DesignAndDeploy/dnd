package edu.teco.dnd.messages;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;
import lime.AgentID;

import edu.teco.dnd.module.Module;

/**
 * This messages is used to advertise that a module is present.
 */
public class GlobalModuleMessage implements Message {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Module";

	/**
	 * Index of the module in the tuple.
	 */
	public static final int MODULE_INDEX = 1;

	/**
	 * Index of the agentID in the tuple.
	 */
	public static final int AGENTID_INDEX = 2;

	/**
	 * Index of the moduleID in the tuple.
	 */
	public static final int MODULEID_INDEX = 3;

	/**
	 * Returns a template tuple for this kind of message.
	 * 
	 * @return a template tuple for this kind of message
	 */
	@Override
	public ITuple getTemplate() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addFormal(Module.class);
		tuple.addFormal(AgentID.class);
		tuple.addFormal(Long.class);
		return tuple;
	}

	/**
	 * The Module that is part of this message.
	 */
	private Module module;

	/**
	 * ID of CommunicationAgent.
	 */
	private AgentID agentID;

	/**
	 * ID of the module.
	 */
	private long moduleID;

	/**
	 * Initializes a new GlobalModuleMessage.
	 * 
	 * @param module
	 *            the Module to advertise
	 * @param agentID
	 *            ID of the CommunicationAgent
	 * @param moduleID
	 *            ID of the module
	 */
	public GlobalModuleMessage(final Module module, final AgentID agentID, final long moduleID) {
		this.module = module;
		this.agentID = agentID;
		this.moduleID = moduleID;
	}

	/**
	 * Initializes a new GlobalModuleMessage and sets the module ID to the ID of the given module or 0L if
	 * null is given.
	 * 
	 * @param module
	 *            the Module to advertise
	 * @param agentID
	 *            ID of the CommunicationAgent
	 */
	public GlobalModuleMessage(final Module module, final AgentID agentID) {
		this(module, agentID, module != null ? module.getID() : 0L);
	}

	/**
	 * Initializes a new GlobalModuleMessage and only sets the module ID.
	 * 
	 * @param moduleID
	 *            the module ID
	 */
	public GlobalModuleMessage(final long moduleID) {
		this(null, null, moduleID);
	}

	/**
	 * Initializes a new GlobalModuleMessage from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * @see #getTemplate()
	 */
	public GlobalModuleMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new GlobalModuleMessage without parameters.
	 */
	public GlobalModuleMessage() {
		this(null, null);
	}

	/**
	 * Returns a tuple that matches messages with the same moduleID.
	 * 
	 * @return a template for messages with the same moduleID.
	 */
	public ITuple getIDTemplate() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addFormal(Module.class);
		tuple.addFormal(AgentID.class);
		tuple.addActual(moduleID);
		return tuple;
	}

	/**
	 * Returns the Module used in this message.
	 * 
	 * @return the Module used in this message
	 */
	public Module getModule() {
		return module;
	}

	/**
	 * Sets the Module to use in this message.
	 * 
	 * @param module
	 *            the Module to use in this message
	 */
	public void setModule(final Module module) {
		this.module = module;
	}

	/**
	 * Returns ID of the CommunicationAgent.
	 * 
	 * @return ID of the CommunicationAgent
	 */
	public AgentID getAgentID() {
		return agentID;
	}

	/**
	 * Sets ID of the CommunicationAgent.
	 * 
	 * @param agentID
	 *            ID of the CommunicationAgent
	 */
	public void setAgentID(final AgentID agentID) {
		this.agentID = agentID;
	}

	/**
	 * Returns the module ID set in this message.
	 * 
	 * @return the module ID set in this message
	 */
	public long getModuleID() {
		return moduleID;
	}

	/**
	 * Sets the module ID for this message.
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
		tuple.addActual(module);
		tuple.addActual(agentID);
		tuple.addActual(moduleID);
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
		module = (Module) fields[MODULE_INDEX].getValue();
		agentID = (AgentID) fields[AGENTID_INDEX].getValue();
		moduleID = (Long) fields[MODULEID_INDEX].getValue();
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
