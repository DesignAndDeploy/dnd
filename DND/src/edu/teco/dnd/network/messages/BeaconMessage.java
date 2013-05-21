package edu.teco.dnd.network.messages;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;


public class BeaconMessage implements Message {
	/**
	 * The type of this message.
	 */
	public static final String MESSAGE_TYPE = "beacon";
	
	/**
	 * The UUID of the module that send this message.
	 */
	private final UUID uuid = null;
	
	/**
	 * The addresses of the module that send this message.
	 */
	private final List<InetSocketAddress> addresses = null;
	
	/**
	 * Initializes a new BeaconMessage.
	 * 
	 * @param uuid the UUID of the module
	 * @param addresses the addresses the module that can be used to initiate a connection. The list is copied to make
	 * 		it unmodifiable
	 */
	public BeaconMessage(final UUID uuid, final List<InetSocketAddress> addresses) {
	}
	
	/**
	 * Constructor without arguments for Gson.
	 */
	@SuppressWarnings("unused")
	private BeaconMessage() {
	}
	
	/**
	 * Returns the UUID of the module.
	 * 
	 * @return the UUID of the module
	 */
	public UUID getUUID() {
		return null;
	}
	
	/**
	 * Returns the addresses that can be used to initiate a connection.
	 * 
	 * @return the addresses of the module
	 */
	public List<InetSocketAddress> getAddresses() {
		return null;
	}
}
