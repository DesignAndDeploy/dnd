/**
 * <p>
 * This package contains an implementation of {@link edu.teco.dnd.network.ConnectionManager} that uses TCP connections
 * and JSON to send Messages.
 * </p>
 * 
 * <p>
 * Messages are sent encoded as JSON objects and are prepended by a 2 byte length field. Each Message object contains an
 * ID and a field <code>type</code> that specifies the class of the Message (although the field is not the class name;
 * see {@link edu.teco.dnd.network.tcp.MessageAdapter}).
 * </p>
 * 
 * <p>
 * When a new connection is established both clients send a {@link edu.teco.dnd.network.messages.HelloMessage} to inform
 * each other about their IDs. The client with the lower ID acts as a master and decides whether or not to keep the
 * connection. If the connection should be kept a {@link edu.teco.dnd.network.messages.ConnectionEstablishedMessage} is
 * sent, otherwise the Channel is closed.
 * </p>
 * 
 * <p>
 * After the initialization phase Messages can be sent by classes using the
 * {@link edu.teco.dnd.network.tcp.TCPConnectionManager}. For this to work the class of the Message that will be sent
 * must be registered at the local and the remote TCPConnectionManager - this should be done as early as possible. For
 * each incoming Message matching handler is looked up. This handler can send a
 * {@link edu.teco.dnd.network.messages.Response} which the client that sent the original Message can easily query.
 * </p>
 * 
 * <p>
 * A note about concurrency: {@link edu.teco.dnd.network.MessageHandler}s may be called multiple times concurrently,
 * even with multiple Messages coming from a single client. Special care has to be taken to make them thread safe.
 * </p>
 */
package edu.teco.dnd.network.tcp;

