package edu.teco.dnd.network.messages;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Message containing all peers known by the sending Module.
 *
 * @author Philipp Adolf
 */
public class PeerMessage implements Message {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(PeerMessage.class);

	public static final String MESSAGE_TYPE = "peers";
	
	private final Map<UUID, Collection<InetSocketAddress>> modules;
	
	public PeerMessage() {
		modules = Collections.emptyMap();
	}
	
	public PeerMessage(final Map<? extends UUID, ? extends Collection<? extends InetSocketAddress>> modules) {
		final Map<UUID, Collection<InetSocketAddress>> m = new HashMap<UUID, Collection<InetSocketAddress>>();
		
		for (final Entry<? extends UUID, ? extends Collection<? extends InetSocketAddress>> entry :
				modules.entrySet()) {
			m.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<InetSocketAddress>(entry.getValue())));
		}
		
		LOGGER.debug(m);
		this.modules = Collections.unmodifiableMap(m);
	}
	
	public Map<UUID, Collection<InetSocketAddress>> getModules() {
		return modules;
	}
}
