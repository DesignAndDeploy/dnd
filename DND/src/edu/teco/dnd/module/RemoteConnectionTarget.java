package edu.teco.dnd.module;

import java.io.Serializable;

import edu.teco.dnd.blocks.ConnectionTarget;

/**
 * A ConnectionTarget that sends its data via a CommunicationAgent to a ConnectionTarget that is on another
 * module.
 */
public class RemoteConnectionTarget extends ConnectionTarget {
	/**
	 * Used to check the version of this class when deserialising.
	 */
	private static final long serialVersionUID = 8125547832369059255L;

	/**
	 * The ID of the FunctionBlock the remote target belongs to.
	 */
	private final String targetFunctionBlock;

	/**
	 * The name of the input on the remote FunctionBlock.
	 */
	private final String targetInput;

	/**
	 * The ApplicationAgent to use.
	 */
	private ApplicationAgent applicationAgent = null;

	/**
	 * The type of data.
	 */
	private final Class<? extends Serializable> type;

	/**
	 * Initializes a new RemoteConnectionTarget.
	 * 
	 * @param name
	 *            the name of the ConnectionTarget
	 * @param targetFunctionBlock
	 *            the functionBlock this Connection
	 * @param targetInput
	 *            the input
	 * @param type
	 *            the type of data
	 */
	public RemoteConnectionTarget(final String name, final String targetFunctionBlock,
			final String targetInput, final Class<? extends Serializable> type) {
		super(name);
		if (targetFunctionBlock == null) {
			throw new IllegalArgumentException("targetFunctionBlock must not be null");
		}
		if (targetInput == null) {
			throw new IllegalArgumentException("targetInput must not be null");
		}
		this.targetFunctionBlock = targetFunctionBlock;
		this.targetInput = targetInput;
		if (type == null) {
			this.type = Serializable.class;
		} else {
			this.type = type;
		}
	}

	/**
	 * Sets the ApplicationAgent to use.
	 * 
	 * @param applicationAgent
	 *            the ApplicationAgent to use
	 */
	public final void setApplicationAgent(final ApplicationAgent applicationAgent) {
		this.applicationAgent = applicationAgent;
	}

	/**
	 * Tells the CommunicationAgent to send a message to the remote ConnectionTarget to update its value.
	 * Nothing is done if no CommunicationAgent has been set.
	 * 
	 * @param value
	 *            the new value
	 */
	@Override
	public final void setValue(final Serializable value) {
		if (applicationAgent != null) {
			applicationAgent.setRemoteValue(targetFunctionBlock, targetInput, value);
		}
	}

	/**
	 * Whether or not this ConnectionTarget is dirty. Should never be called as this is only used in Outputs,
	 * not as a real ConnectionTarget.
	 * 
	 * @return whether or not this ConnectionTarget is dirty
	 */
	@Override
	public final boolean isDirty() {
		return false;
	}

	/**
	 * Updates the Field on the corresponding FunctionBlock. As this ConnectionTarget is not directly
	 * connected to a FunctionBlock, this method does nothing.
	 */
	@Override
	public final void update() {
	}

	@Override
	public final Class<? extends Serializable> getType() {
		return type;
	}
}
