package edu.teco.dnd.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.util.NetConnection;

/**
 * A simple read-only implementation of {@link AddressBasedServerConfig}.
 */
public class SimpleAddressBasedServerConfig implements AddressBasedServerConfig {
	private final ModuleID moduleID;
	private final Collection<InetSocketAddress> listenAddresses;
	private final Collection<NetConnection> multicastAddresses;
	private final Collection<InetSocketAddress> announceAddresses;
	private final int announceInterval;

	/**
	 * Initializes a new AddressBasedServerConfig.
	 * 
	 * @param moduleID
	 *            the ID of the Module
	 * @param listenAddresses
	 *            the addresses the servers should listen on
	 * @param multicastAddresses
	 *            the multicast addresses that should be used to send out the servers addresses
	 * @param announceAddresses
	 *            the addresses to send out to the multicast addresses
	 * @param announceInterval
	 *            time between two announcements
	 */
	public SimpleAddressBasedServerConfig(final ModuleID moduleID, final Collection<InetSocketAddress> listenAddresses,
			final Collection<NetConnection> multicastAddresses, final Collection<InetSocketAddress> announceAddresses,
			int announceInterval) {
		this.moduleID = moduleID;
		this.listenAddresses = Collections.unmodifiableCollection(new ArrayList<InetSocketAddress>(listenAddresses));
		this.multicastAddresses = Collections.unmodifiableCollection(new ArrayList<NetConnection>(multicastAddresses));
		this.announceAddresses =
				Collections.unmodifiableCollection(new ArrayList<InetSocketAddress>(announceAddresses));
		this.announceInterval = announceInterval;
	}

	@Override
	public ModuleID getModuleID() {
		return moduleID;
	}

	@Override
	public Collection<InetSocketAddress> getListenAddresses() {
		return listenAddresses;
	}

	@Override
	public Collection<NetConnection> getMulticastAddresses() {
		return multicastAddresses;
	}

	@Override
	public Collection<InetSocketAddress> getAnnounceAddresses() {
		return announceAddresses;
	}

	@Override
	public int getAnnounceInterval() {
		return announceInterval;
	}

	@Override
	public String toString() {
		return "AddressBasedServerConfig[moduleID=" + moduleID + ",listenAddresses=" + listenAddresses
				+ ",multicastAddresses=" + multicastAddresses + ",announceAddresses=" + announceAddresses
				+ ",announceInterval=" + announceInterval + "]";
	}
}
