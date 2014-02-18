package edu.teco.dnd.module.config;

import java.net.InetSocketAddress;
import java.util.Collection;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.util.NetConnection;

public abstract class ModuleConfig {
	public abstract ModuleID getModuleID();

	public abstract String getName();

	public abstract String getLocation();

	public abstract Collection<InetSocketAddress> getListen();

	public abstract Collection<InetSocketAddress> getAnnounce();

	public abstract Collection<NetConnection> getMulticast();

	public abstract int getAnnounceInterval();

	public abstract int getMaxThreadsPerApp();

	public abstract BlockTypeHolder getBlockRoot();
}
