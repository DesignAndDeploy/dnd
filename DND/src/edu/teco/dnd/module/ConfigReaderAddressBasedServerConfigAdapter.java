package edu.teco.dnd.module;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;

import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.server.AddressBasedServerConfig;
import edu.teco.dnd.util.NetConnection;

public class ConfigReaderAddressBasedServerConfigAdapter implements AddressBasedServerConfig {
	private final ConfigReader configReader;
	
	public ConfigReaderAddressBasedServerConfigAdapter(final ConfigReader configReader) {
		this.configReader = configReader;
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
