package edu.teco.dnd.network.messages;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import edu.teco.dnd.module.ModuleID;

public class BeaconMessage extends Message {
	/**
	 * The type of this message.
	 */
	public static final String MESSAGE_TYPE = "beacon";

	/**
	 * The UUID of the module that send this message.
	 */
	private final ModuleID moduleID;

	/**
	 * The addresses of the module that send this message.
	 */
	private final List<InetSocketAddress> addresses;

	/**
	 * Initializes a new BeaconMessage.
	 * 
	 * @param uuid
	 *            the UUID of this Message
	 * @param moduleID
	 *            the ID of the module
	 * @param addresses
	 *            the addresses the module that can be used to initiate a connection. The list is copied to make it
	 *            unmodifiable. Can be null to use an empty list.
	 */
	public BeaconMessage(final UUID uuid, final ModuleID moduleID, final List<InetSocketAddress> addresses) {
		super(uuid);
		this.moduleID = moduleID;
		if (addresses == null) {
			this.addresses = Collections.unmodifiableList(Collections.<InetSocketAddress> emptyList());
		} else {
			this.addresses = Collections.unmodifiableList(new ArrayList<InetSocketAddress>(addresses));
		}
	}

	/**
	 * Initializes a new BeaconMessage.
	 * 
	 * @param moduleID
	 *            the ID of the module
	 * @param addresses
	 *            the addresses the module that can be used to initiate a connection. The list is copied to make it
	 *            unmodifiable. Can be null to use an empty list.
	 */
	public BeaconMessage(final ModuleID moduleID, final List<InetSocketAddress> addresses) {
		this.moduleID = moduleID;
		if (addresses == null) {
			this.addresses = Collections.unmodifiableList(Collections.<InetSocketAddress> emptyList());
		} else {
			this.addresses = Collections.unmodifiableList(new ArrayList<InetSocketAddress>(addresses));
		}
	}

	/**
	 * Constructor without arguments for Gson.
	 */
	@SuppressWarnings("unused")
	private BeaconMessage() {
		this(null, null);
	}

	public ModuleID getModuleID() {
		return moduleID;
	}

	/**
	 * Returns the addresses that can be used to initiate a connection.
	 * 
	 * @return the addresses of the module
	 */
	public List<InetSocketAddress> getAddresses() {
		return addresses;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("BeaconMessage[uuid=").append(moduleID).append(",addresses=").append(addresses).append("]");
		return sb.toString();
	}
}
