package edu.teco.dnd.module.config;

import java.net.InetSocketAddress;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.util.NetConnection;

public abstract class ModuleConfig {
	public abstract ModuleID getModuleID();

	public abstract String getName();

	public abstract String getLocation();

	public abstract InetSocketAddress[] getListen();

	public abstract InetSocketAddress[] getAnnounce();

	public abstract NetConnection[] getMulticast();

	public abstract int getAnnounceInterval();

	public abstract int getMaxThreadsPerApp();

	public abstract BlockTypeHolder getBlockRoot();
}
