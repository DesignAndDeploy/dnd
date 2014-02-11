package edu.teco.dnd.util;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;

public class NetConnection {
	private final InetSocketAddress address;
	private final NetworkInterface interf;

	public NetConnection(final InetSocketAddress address, final NetworkInterface interf) {
		this.address = address;
		this.interf = interf;
	}

	public NetConnection() {
		this(null, null);
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public NetworkInterface getInterface() {
		return interf;
	}

	@Override
	public String toString() {
		return "NetConnection[address=" + address + ",interf=" + interf + "]";
	}
}