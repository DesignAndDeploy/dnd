package edu.teco.dnd.module;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;

import edu.teco.dnd.module.config.ModuleConfig;
import edu.teco.dnd.server.AddressBasedServerConfig;
import edu.teco.dnd.util.NetConnection;

public class ModuleConfigAddressBasedServerConfigAdapter implements AddressBasedServerConfig {
	private final ModuleConfig configReader;
	
	public ModuleConfigAddressBasedServerConfigAdapter(final ModuleConfig moduleConfig) {
		this.configReader = moduleConfig;
	}
	
	@Override
	public ModuleID getModuleID() {
		return configReader.getModuleID();
	}

	@Override
	public int getAnnounceInterval() {
		return configReader.getAnnounceInterval();
	}

	@Override
	public Collection<InetSocketAddress> getListenAddresses() {
		return Arrays.asList(configReader.getListen());
	}

	@Override
	public Collection<NetConnection> getMulticastAddresses() {
		return Arrays.asList(configReader.getMulticast());
	}

	@Override
	public Collection<InetSocketAddress> getAnnounceAddresses() {
		return Arrays.asList(configReader.getAnnounce());
	}
}
