package edu.teco.dnd.network;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Executor;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.FutureNotifier;

/**
 * A ConnectionManager is used to create and use connections between modules. It is responsible for initiating
 * connections, accepting incoming connections, routing incoming messages to the appropriate handlers and sending
 * messages to the right modules.
 */
public interface ConnectionManager {
	/**
	 * The application ID that is used if no application ID was specified.
	 */
	public static final ApplicationID APPID_DEFAULT = new ApplicationID(
			UUID.fromString("00000000-0000-0000-0000-000000000000"));

	/**
	 * Sends a message to the given Module.
	 * 
	 * @param moduleID
	 *            the ID of the module the message should be sent to
	 * @param message
	 *            the message that should be sent
	 * @return a FutureNotifier that will return the Response for the message. If this method is called to send a
	 *         Response the FutureNotifier will only reflect whether or not sending the Response to the connected client
	 *         has succeeded. The result will always be null in this case.
	 */
	public FutureNotifier<Response> sendMessage(ModuleID moduleID, Message message);

	/**
	 * Adds an handler for a given application ID. If another handler was registered for the ID it is replaced.
	 * 
	 * @param <T>
	 *            type of Message that should be handled
	 * @param applicationID
	 *            the ID of the application. Use {@link #APPID_DEFAULT} for non application messages
	 * @param msgType
	 *            the class of Messages that the handler will receive. Must be an exact match.
	 * @param handler
	 *            the handler that should receive the Messages
	 * @param executor
	 *            the executor that should execute {@link MessageHandler#handleMessage(Message)}. Can be null.
	 */
	public <T extends Message> void addHandler(ApplicationID applicationID, Class<? extends T> msgType,
			MessageHandler<? super T> handler, Executor executor);

	/**
	 * Adds an handler for a given application ID. If another handler was registered for the ID it is replaced.
	 * 
	 * @param <T>
	 *            type of Message that should be handled
	 * @param appid
	 *            the ID of the application. Use {@link #APPID_DEFAULT} for non application messages
	 * @param msgType
	 *            the class of Messages that the handler will receive. Must be an exact match.
	 * @param handler
	 *            the handler that should receive the Messages
	 */
	public <T extends Message> void addHandler(ApplicationID appid, Class<? extends T> msgType,
			MessageHandler<? super T> handler);

	/**
	 * Adds an handler for the {@link #APPID_DEFAULT}. If another handler was registered for the ID it is replaced.
	 * 
	 * @param <T>
	 *            type of Message that should be handled
	 * @param msgType
	 *            the class of Messages that the handler will receive. Must be an exact match.
	 * @param handler
	 *            the handler that should receive the Messages
	 * @param executor
	 *            the executor that should execute {@link MessageHandler#handleMessage(Message)}. Can be null.
	 */
	public <T extends Message> void addHandler(Class<? extends T> msgType, MessageHandler<? super T> handler,
			Executor executor);

	/**
	 * Adds an handler for the {@link #APPID_DEFAULT}. If another handler was registered for the ID it is replaced.
	 * 
	 * @param <T>
	 *            type of Message that should be handled
	 * @param appid
	 *            the ID of the application. Use {@link #APPID_DEFAULT} for non application messages
	 * @param msgType
	 *            the class of Messages that the handler will receive. Must be an exact match.
	 * @param handler
	 *            the handler that should receive the Messages
	 */
	public <T extends Message> void addHandler(Class<? extends T> msgType, MessageHandler<? super T> handler);

	/**
	 * Returns a collection of connected modules.
	 * 
	 * @return a collection of connected modules
	 */
	public Collection<ModuleID> getConnectedModules();

	/**
	 * Adds a listener that is informed if new connections are made or old connections are lost.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addConnectionListener(ConnectionListener listener);

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeConnectionListener(ConnectionListener listener);

	/**
	 * Tells this TCPConnectionManager to shut down. This will close all listening sockets and all connections to other
	 * TCPConnectionManagers.
	 * 
	 * @see #isShuttingDown()
	 * @see #isShutDown()
	 */
	public void shutdown();

	/**
	 * Returns true if this TCPConnectionManager is shutting down or has finished shutting down. This basically returns
	 * whether or not {@link #shutdown()} has been called.
	 * 
	 * @return true if this TCPConnectionManager is shutting down or has finished shutting down
	 */
	public boolean isShuttingDown();

	/**
	 * Returns a Future that is done when the ConnectionManager has shut down.
	 * 
	 * @return a Future that is done when the ConnectionManager has shut down
	 */
	public FutureNotifier<Void> getShutdownFuture();
}
