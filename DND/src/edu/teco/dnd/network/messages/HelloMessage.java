package edu.teco.dnd.network.messages;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleID;

/**
 * This message is sent when a new connection is created to tell the other {@link Module} about the {@link ModuleID}.
 */
public class HelloMessage extends Message {
	public static final String MESSAGE_TYPE = "hello";

	private final ModuleID moduleID;

	/**
	 * The maximum frame size the sending module can receive.
	 */
	private final int framesize;

	/**
	 * Initializes a new HelloMessage.
	 * 
	 * @param moduleID
	 *            the ID of the sending {@link Module}
	 * @param framesize
	 *            the maximum frame size the Module can receive
	 */
	public HelloMessage(final ModuleID moduleID, final int framesize) {
		this.moduleID = moduleID;
		this.framesize = framesize;
	}

	/**
	 * Returns the ID of the sending module.
	 * 
	 * @return the ID of the sending module
	 */
	public ModuleID getModuleID() {
		return this.moduleID;
	}

	/**
	 * Returns the maximum frame size the module can receive.
	 * 
	 * @return the maximum frame size the module can receive
	 */
	public int getFramesize() {
		return this.framesize;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("HelloMessage[moduleID=").append(moduleID).append(",framesize=").append(framesize).append("]");
		return sb.toString();
	}
}
