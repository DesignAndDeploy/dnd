package edu.teco.dnd.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import edu.teco.dnd.util.NetConnection;

/**
 * A ServerConfig that provides three sets of addresses: listen, multicast and announce. Listen addresses are those the
 * server(s) should use for listening for incoming connections. The multicast addresses should be used to broadcast the
 * servers addresses. The addresses that should be announced are stored in announce. These are normally the same as the
 * ones used for listen, however it may be useful to add external addresses (for example, the address of a router doing
 * NAT if it has been configured to forward the necessary ports).
 */
public class AddressBasedServerConfig implements ServerConfig {
	private final UUID moduleUUID;
	private final Collection<InetSocketAddress> listenAddresses;
	private final Collection<NetConnection> multicastAddresses;
	private final Collection<InetSocketAddress> announceAddresses;
	private final int announceInterval;

	/**
	 * Initializes a new AddressBasedServerConfig.
	 * 
	 * @param moduleUUID
	 *            the UUID of the Module
	 * @param listenAddresses
	 *            the addresses the servers should listen on
	 * @param multicastAddresses
	 *            the multicast addresses that should be used to send out the servers addresses
	 * @param announceAddresses
	 *            the addresses to send out to the multicast addresses
	 * @param announceInterval
	 *            time between two announcements
	 */
	public AddressBasedServerConfig(final UUID moduleUUID, final Collection<InetSocketAddress> listenAddresses,
			final Collection<NetConnection> multicastAddresses, final Collection<InetSocketAddress> announceAddresses,
			int announceInterval) {
		this.moduleUUID = moduleUUID;
		this.listenAddresses = Collections.unmodifiableCollection(new ArrayList<InetSocketAddress>(listenAddresses));
		this.multicastAddresses = Collections.unmodifiableCollection(new ArrayList<NetConnection>(multicastAddresses));
		this.announceAddresses =
				Collections.unmodifiableCollection(new ArrayList<InetSocketAddress>(announceAddresses));
		this.announceInterval = announceInterval;
	}

	@Override
	public UUID getModuleUUID() {
		return moduleUUID;
	}

	public Collection<InetSocketAddress> getListenAddresses() {
		return listenAddresses;
	}

	public Collection<NetConnection> getMulticastAddresses() {
		return multicastAddresses;
	}

	public Collection<InetSocketAddress> getAnnounceAddresses() {
		return announceAddresses;
	}

	@Override
	public int getAnnounceInterval() {
		return announceInterval;
	}

	@Override
	public String toString() {
		return "AddressBasedServerConfig[moduleUUID=" + moduleUUID + ",listenAddresses=" + listenAddresses
				+ ",multicastAddresses=" + multicastAddresses + ",announceAddresses=" + announceAddresses
				+ ",announceInterval=" + announceInterval + "]";
	}
}
