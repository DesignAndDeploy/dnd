package edu.teco.dnd.server;

import java.net.InetSocketAddress;
import java.util.Collection;

import edu.teco.dnd.util.NetConnection;

/**
 * A ServerConfig that provides three sets of addresses: listen, multicast and announce. Listen addresses are those the
 * server(s) should use for listening for incoming connections. The multicast addresses should be used to broadcast the
 * servers addresses. The addresses that should be broadcast are stored in announce. These are normally the same as the
 * ones used for listen, however it may be useful to add external addresses (for example, the address of a router doing
 * NAT if it has been configured to forward the necessary ports).
 */
public interface AddressBasedServerConfig extends ServerConfig {
	Collection<InetSocketAddress> getListenAddresses();

	Collection<NetConnection> getMulticastAddresses();

	Collection<InetSocketAddress> getAnnounceAddresses();
}