package edu.teco.dnd.network;

import java.util.Map;

import edu.teco.dnd.network.messages.Message;

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
	void handleMessage(Message message);
	
	/**
	 * Returns a list of all Message types this handler uses (either receiving or sending). The value can be null; if
	 * that is the case reflection is used.
	 * 
	 * @return a map containing all message types this handler uses
	 */
	Map<Class<? extends Message>, String> getMessageTypes();
}
