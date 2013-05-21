package edu.teco.dnd.network;

import java.util.List;
import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

/**
 * A ConnectionManager is used to create and use connections between modules. It is responsible for initiating
 * connections, accepting incoming connections, route incoming messages to the appropriate handlers and send
 * messages to the right modules.
 * 
 * @author Philipp Adolf
 */
public interface ConnectionManager {
	/**
	 * The application ID that is used if no application ID was specified.
	 */
	public static final UUID APPID_DEFAULT = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	/**
	 * Sends a message to the given Module.
	 * 
	 * @param uuid the UUID of the module the message should be sent to
	 * @param message the message that should be sent
	 */
	public void sendMessage(UUID uuid, Message message);
	
	/**
	 * Adds an handler for a given application ID. If another handler was registered for the ID it is replaced.
	 * 
	 * @param appid the application ID the handler should be used for
	 * @param handler the handler for the application ID. Use null to remove the handler.
	 */
	public void addHandler(UUID appid, MessageHandler handler);
	
	/**
	 * Registers a handler for {@link #APPID_DEFAULT}.
	 * 
	 * @param handler the handler to register
	 * @see #addHandler(UUID, MessageHandler)
	 */
	public void addHandler(MessageHandler handler);
	
	/**
	 * Returns a list of connected modules.
	 * 
	 * @return a list of connected modules
	 */
	public List<UUID> getConnectedModules();
}
