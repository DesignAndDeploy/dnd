/**
 * The network layer for {@link edu.teco.dnd.module.Module} and the {@link edu.teco.dnd.eclipse.Activator Eclipse
 * plugin}.
 * 
 * The main class of the network layer is {@link edu.teco.dnd.network.ConnectionManager} (or rather: its
 * implementations). It handles creating connections to other Modules as well as sending and receiving
 * {@link edu.teco.dnd.network.messages.Message}s.
 * 
 * Another important class is {@link edu.teco.dnd.network.UDPMulticastBeacon} which handles autodiscovery of Modules.
 */
package edu.teco.dnd.network;