package edu.teco.dnd.network;

/**
 * A listener that will be informed about new messages.
 * 
 * @author Philipp Adolf
 */
public interface MessageHandler {
	/**
	 * This method is called if a new message is received. This method may be called from multiple Threads
	 * simultaneously.
	 * 
	 * @param message the message that was received
	 */
	public void handleMessage(Message message);
}
