package edu.teco.dnd.network.messages;

import java.util.UUID;

/**
 * This message is sent if a new connection was made to inform the other end who we are.
 *
 * @author Philipp Adolf
 */
public class HelloMessage implements Message {
	/**
	 * The type of this message.
	 */
	public static final String MESSAGE_TYPE = "hello";
	
	/**
	 * The UUID of the module sending the message.
	 */
	private final UUID uuid;
	
	/**
	 * The maximum frame size the sending module can receive.
	 */
	private final int framesize;
	
	/**
	 * Initializes a new HelloMessage.
	 * 
	 * @param uuid the UUID of the sending Module
	 * @param framesize the maximum frame size the Module can receive
	 */
	public HelloMessage(final UUID uuid, final int framesize) {
		this.uuid = uuid;
		this.framesize = framesize;
	}
	
	/**
	 * Returns the UUID of the sending module.
	 * 
	 * @return the UUID of the sending module
	 */
	public UUID getUUID() {
		return this.uuid;
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
		sb.append("HelloMessage[uuid=")
			.append(uuid)
			.append(",framesize=")
			.append(framesize)
			.append("]");
		return sb.toString();
	}
}
