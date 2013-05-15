package edu.teco.dnd.messages;

import java.util.UUID;

import lights.adapters.Tuple;
import lights.interfaces.IField;
import lights.interfaces.ITuple;
import lime.AgentID;

/**
 * This message is used to signal that a specific Module is running a specific FunctionBlock.
 */
public class ApplicationBlockMessage implements Message {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Block";

	/**
	 * Index of the agentID in the tuple.
	 */
	public static final int AGENTID_INDEX = 1;
	/**
	 * Index of the blockID in the tuple.
	 */
	public static final int BLOCKID_INDEX = 2;
	/**
	 * Index of the uid in the tuple.
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
		tuple.addFormal(AgentID.class);
		tuple.addFormal(String.class);
		tuple.addFormal(Long.class);
		return tuple;
	}

	/**
	 * The AgentID of the ApplicationAgent the FunctionBlock is running on.
	 */
	private AgentID agentID;

	/**
	 * The ID of the FunctionBlock.
	 */
	private String blockID;

	/**
	 * UID used for managing the message, as lights is buggy and won't properly retrieve tuples containing
	 * ValueHolders.
	 */
	private Long uid;

	/**
	 * Initializes a new ApplicationBlockMessage.
	 * 
	 * @param agentID
	 *            the ID of the CommunicationAgent that the FunctionBlock is running on
	 * @param blockID
	 *            the ID of the FunctionBlock
	 */
	public ApplicationBlockMessage(final AgentID agentID, final String blockID) {
		this.agentID = agentID;
		this.blockID = blockID;
		uid = UUID.randomUUID().getLeastSignificantBits();
	}

	/**
	 * Initializes a new ApplicationBlockMessage.
	 * 
	 * @param blockID
	 *            the ID of the FunctionBlock
	 */
	public ApplicationBlockMessage(final String blockID) {
		this(null, blockID);
	}

	/**
	 * Initializes a new AppilcationBlockMessage from a tuple.
	 * 
	 * @param tuple
	 *            the tuple to get the data from, should be valid
	 * 
	 * @see #getTemplate()
	 */
	public ApplicationBlockMessage(final ITuple tuple) {
		setTuple(tuple);
	}

	/**
	 * Initializes a new ApplicationBlockMessage without parameters.
	 */
	public ApplicationBlockMessage() {
		this(null, null);
	}

	/**
	 * Returns a tuple that can be used to find the AgentID for a given block ID.
	 * 
	 * @return a tuple that can be used to find the AgentID for a given block ID
	 */
	public ITuple getBlockIDTemplate() {
		ITuple tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addFormal(AgentID.class);
		tuple.addActual(blockID);
		tuple.addFormal(Long.class);
		return tuple;
	}

	/**
	 * Returns the AgentID of the CommunicationAgent the FunctionBlock is running on.
	 * 
	 * @return the AgentID of the CommunicationAgent the FunctionBlock is running on
	 */
	public AgentID getAgentID() {
		return agentID;
	}

	/**
	 * Sets the AgentID of the CommunicationAgent the FunctionBlock is running on.
	 * 
	 * @param agentID
	 *            the AgentID of the CommunicationAgent the FunctionBlock is running on
	 */
	public void setAgentID(final AgentID agentID) {
		this.agentID = agentID;
	}

	/**
	 * Returns the ID of the FunctionBlock.
	 * 
	 * @return the ID of the FunctionBlock
	 */
	public String getBlockID() {
		return blockID;
	}

	/**
	 * Sets the ID of the FunctionBlock.
	 * 
	 * @param blockID
	 *            the ID of the FunctionBlock
	 */
	public void setBlockID(final String blockID) {
		this.blockID = blockID;
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
		tuple.addActual(this.agentID);
		tuple.addActual(this.blockID);
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
		tuple.addFormal(AgentID.class);
		tuple.addActual(this.blockID);
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
		this.agentID = (AgentID) fields[AGENTID_INDEX].getValue();
		this.blockID = (String) fields[BLOCKID_INDEX].getValue();
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
