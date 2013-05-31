package edu.teco.dnd.eclipse;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioDatagramChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.TCPConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;

public class Activator extends AbstractUIPlugin {
	private static Activator plugin;
	
	private UUID uuid;
	
	private ConnectionManager connectionManager;
	
	private UDPMulticastBeacon beacon;
	
	public static Activator getDefault() {
		return plugin;
	}
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		uuid = UUID.randomUUID();
		
		final NioEventLoopGroup networkEventLoopGroup = new NioEventLoopGroup();
		
		final TCPConnectionManager connectionManager = new TCPConnectionManager(networkEventLoopGroup,
				networkEventLoopGroup,
				new ChannelFactory<NioServerSocketChannel>() {
					@Override
					public NioServerSocketChannel newChannel() {
						return new NioServerSocketChannel();
					}
				},
				new ChannelFactory<NioSocketChannel>() {
					@Override
					public NioSocketChannel newChannel() {
						return new NioSocketChannel();
					}
				},
				uuid
			);
		this.connectionManager = connectionManager;
		
		beacon = new UDPMulticastBeacon(new ChannelFactory<OioDatagramChannel>() {
			@Override
			public OioDatagramChannel newChannel() {
				return new OioDatagramChannel();
			}
		}, new OioEventLoopGroup(), networkEventLoopGroup, uuid);
		beacon.addListener(connectionManager);
		final List<InetSocketAddress> announce = new ArrayList<InetSocketAddress>();
		for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				interfaces.hasMoreElements(); ) {
			final NetworkInterface interf = interfaces.nextElement();
			for (final Enumeration<InetAddress> addresses = interf.getInetAddresses(); addresses.hasMoreElements(); ) {
				final InetAddress address = addresses.nextElement();
				announce.add(new InetSocketAddress(address, 5000));
			}
		}
		beacon.setAnnounceAddresses(announce);
		
		
		networkEventLoopGroup.execute(new Runnable() {
			@Override
			public void run() {
				connectionManager.startListening(new InetSocketAddress(5000));
				try {
					beacon.addAddress(new InetSocketAddress("225.0.0.1", 5000));
				} catch (final SocketException e) {
					e.printStackTrace();
				}				
			}
		});
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		
		// TODO: shutdown
	}
	
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}
	
	public UDPMulticastBeacon getMulticastBeacon() {
		return beacon;
	}
	
	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store){
		store.setDefault("AddrAndPorts", "bli");
		store.setDefault("Multicast", "bla");
		store.setDefault("MulticastContent", "blubb");
	}
}
