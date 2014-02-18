package edu.teco.dnd.module.config;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Collection;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.module.ModuleMain;
import edu.teco.dnd.util.NetConnection;

/**
 * A configuration for a {@link Module}.
 * 
 * @see ModuleMain
 */
public interface ModuleConfig {
	/**
	 * The ID for the {@link Module}.
	 * 
	 * @return the ID for the Module
	 */
	public abstract ModuleID getModuleID();

	/**
	 * The name for the {@link Module}.
	 * 
	 * @return the name for the Module
	 */
	public abstract String getName();

	/**
	 * The location of the {@link Module}.
	 * 
	 * @return the location of the Module
	 */
	public abstract String getLocation();

	/**
	 * The listen addresses of the {@link Module}. These are the addresses the servers will actively listen on.
	 * 
	 * @return the listen addresses of the Module
	 */
	public abstract Collection<InetSocketAddress> getListen();

	/**
	 * The announce addresses of the {@link Module}. These are the addresses the Module will send out via multicast for
	 * other Modules to connect to. These can be different than the {@link #getListen() listen addresses} if, for
	 * example, there’s a NAT between the modules. In that case the announce addresses should contain the external
	 * address.
	 * 
	 * @return the announce addresses of the Module
	 */
	public abstract Collection<InetSocketAddress> getAnnounce();

	/**
	 * The multicast addresses of the {@link Module}. These contain of an address that should be in the multicast range
	 * (for IPv4 or IPv6), a port to use and a {@link NetworkInterface} on which the multicast should be sent.
	 * 
	 * @return the multicast addresses of the Module
	 */
	public abstract Collection<NetConnection> getMulticast();

	/**
	 * Returns the announce interval of the {@link Module}. This is the time between to multicasts of the
	 * {@link #getAnnounce() announce addresses}.
	 * 
	 * @return the announce interval of the Module
	 */
	public abstract int getAnnounceInterval();

	/**
	 * Returns the maximum number of Threads that may be used by an {@link Application}.
	 * 
	 * @return the maximum number of Threads that may be used by an Application
	 */
	public abstract int getMaxThreadsPerApp();

	/**
	 * Returns the root of the {@link BlockTypeHolder} tree that should be used by the {@link Module}. Note that the
	 * BlockTypeHolders themselves may be modified.
	 * 
	 * @return the root of the BlockTypeHolder tree
	 */
	public abstract BlockTypeHolder getBlockRoot();
}
