package edu.teco.dnd.discover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lights.adapters.Tuple;
import lights.interfaces.ITuple;
import lime.AgentCreationException;
import lime.AgentID;
import lime.AgentLocation;
import lime.HostLocation;
import lime.IllegalTupleSpaceNameException;
import lime.LimeServer;
import lime.LimeServerID;
import lime.LimeSystemTupleSpace;
import lime.LimeTupleSpace;
import lime.TupleSpaceEngineException;

import edu.teco.dnd.messages.ApplicationModuleMessage;
import edu.teco.dnd.messages.GlobalApplicationMessage;
import edu.teco.dnd.messages.GlobalModuleMessage;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.util.RunnerAgent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class responsible for discovering the modules in the network. Abstraction of the whole discover
 * sub-system.
 * 
 * @author peter
 */
public class Discover extends RunnerAgent {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 2777051463787673620L;

	/**
	 * Logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Discover.class);

	/**
	 * The instance of this class that should be used.
	 */
	private static Discover singleton = null;

	/**
	 * Used for access to {@link #listener}.
	 */
	private final Lock listenerLock = new ReentrantLock();

	/**
	 * Objects that want to be informed if a call has been finished.
	 */
	private final Set<DiscoverListener> listener = new HashSet<>();

	/**
	 * The system tuple space that holds information about all connected servers and agents.
	 */
	private LimeSystemTupleSpace systemSpace = null;

	/**
	 * The module space where modules and applications are announced.
	 */
	private LimeTupleSpace moduleSpace = null;

	/**
	 * Returns the instance of Discover to use. If no instance is available one is loaded with LimeServer.
	 * Returns null on error.
	 * 
	 * @return the instance of Discover to use or null if an error occurred during loading
	 */
	public static synchronized Discover getSingleton() {
		if (singleton == null) {
			LOGGER.info("loading Discover agent");
			try {
				singleton = (Discover) LimeServer.getServer().loadAgent(Discover.class.getName(), null);
			} catch (AgentCreationException e) {
				LOGGER.catching(e);
				LOGGER.error("creating discover failed");
			}
		}
		return singleton;
	}

	/**
	 * The constructor has to be public for Lime to be able to load it. It should not be used directly, use
	 * {@link #getSingleton()} instead.
	 */
	public Discover() {
	}

	@Override
	public void doRun() {
		setName("discover");
		try {
			systemSpace = new LimeSystemTupleSpace();
			moduleSpace = new LimeTupleSpace("ModuleSpace");
			moduleSpace.setShared(true);
			LimeServer.getServer().engage();
		} catch (
				IllegalTupleSpaceNameException | TupleSpaceEngineException e) {
			LOGGER.catching(e);
		}
	}

	/**
	 * Returns all tuples from all servers in the given tuple space.
	 * 
	 * @param space
	 *            the space to search through
	 * @param template
	 *            the template to look for
	 * @return all tuples from all servers matching the template
	 * @throws TupleSpaceEngineException
	 *             if an error occurs in the tuple space engine
	 */
	private List<ITuple> getAllTuples(final LimeTupleSpace space, final ITuple template)
			throws TupleSpaceEngineException {
		LOGGER.entry(space, template);
		ITuple[] spaces = systemSpace.rdg(new Tuple().addFormal(Object.class).addActual(space.getName())
				.addFormal(AgentID.class));
		if (spaces == null) {
			LOGGER.exit(Collections.emptyList());
			return Collections.emptyList();
		}
		Set<LimeServerID> servers = new HashSet<>();
		for (ITuple tuple : spaces) {
			servers.add(((AgentID) tuple.get(2).getValue()).getLimeServerID());
		}
		ArrayList<ITuple> tuples = new ArrayList<>();
		for (LimeServerID server : servers) {
			LOGGER.debug("checking {}", server);
			ITuple[] ts = space.rdg(new HostLocation(server), AgentLocation.UNSPECIFIED, template);
			if (ts != null) {
				tuples.ensureCapacity(tuples.size() + ts.length);
				for (ITuple t : ts) {
					tuples.add(t);
				}
			}
		}
		LOGGER.exit(tuples);
		return tuples;
	}

	/**
	 * Searches for all modules and calls {@link DiscoverListener#modulesDiscovered(Map)} on all registered
	 * listeners.
	 */
	private void discoverModules() {
		LOGGER.entry();
		Map<AgentID, Module> modules = new HashMap<>();
		try {
			for (ITuple tuple : getAllTuples(moduleSpace, new GlobalModuleMessage().getTemplate())) {
				GlobalModuleMessage gmm = new GlobalModuleMessage(tuple);
				modules.put(gmm.getAgentID(), gmm.getModule());
			}
		} catch (TupleSpaceEngineException e) {
			LOGGER.warn("discovery failed {}", e);
			return;
		}
		LOGGER.trace("acquiring lock");
		listenerLock.lock();
		try {
			for (DiscoverListener l : listener) {
				LOGGER.debug("calling {}", l);
				l.modulesDiscovered(modules);
			}
		} finally {
			listenerLock.unlock();
			LOGGER.trace("releasing lock");
		}
		LOGGER.exit();
	}

	/**
	 * Enqueues a search for modules. Listeners will be notified via
	 * {@link DiscoverListener#modulesDiscovered(Map)}.
	 */
	public void startModuleDiscovery() {
		LOGGER.entry();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				discoverModules();
			}
		};
		addRunnable(runnable);
		LOGGER.exit();
	}

	/**
	 * Searches for all applications and calls {@link DiscoverListener#applicationsDiscovered(Map)} on all
	 * registered listeners.
	 */
	private void discoverApplications() {
		LOGGER.entry();
		Map<Integer, String> ids = new HashMap<>();
		try {
			for (ITuple tuple : getAllTuples(moduleSpace, new GlobalApplicationMessage().getTemplate())) {
				GlobalApplicationMessage gam = new GlobalApplicationMessage(tuple);
				LOGGER.debug("found {}", gam);
				ids.put(gam.getApplicationID(), gam.getName());
			}
		} catch (TupleSpaceEngineException e) {
			LOGGER.warn("application discovery failed {}", e);
			return;
		}
		LOGGER.trace("acquiring lock");
		listenerLock.lock();
		try {
			for (DiscoverListener l : listener) {
				LOGGER.debug("calling {}", l);
				l.applicationsDiscovered(ids);
			}
		} finally {
			listenerLock.unlock();
			LOGGER.trace("releasing lock");
		}
		LOGGER.exit();
	}

	/**
	 * Enqueues a search for applications. Listeners will be notified via
	 * {@link DiscoverListener#applicationsDiscovered(Map)}.
	 */
	public void startApplicationDiscovery() {
		LOGGER.entry();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				discoverApplications();
			}
		};
		addRunnable(runnable);
		LOGGER.exit();
	}

	/**
	 * Enqueues a search for modules in the given application. Listeners will be notified via
	 * {@link DiscoverListener#applicationModulesDiscovered(int, Map)}.
	 * 
	 * @param id
	 *            the id of the application to search in
	 */
	private void scanApplication(final int id) {
		LOGGER.entry(id);
		Map<AgentID, Long> modules = new HashMap<>();
		LimeTupleSpace applicationSpace = null;
		try {
			applicationSpace = new LimeTupleSpace("ApplicationSpace" + id);
		} catch (
				IllegalTupleSpaceNameException | TupleSpaceEngineException e) {
			LOGGER.catching(Level.WARN, e);
			LOGGER.warn("failed to join application space {}", id);
			LOGGER.exit();
			return;
		}
		applicationSpace.setShared(true);
		LimeServer.getServer().engage();
		try {
			for (ITuple tuple : getAllTuples(applicationSpace, new ApplicationModuleMessage().getTemplate())) {
				ApplicationModuleMessage amm = new ApplicationModuleMessage(tuple);
				modules.put(amm.getAgentID(), amm.getModuleID());
			}
		} catch (TupleSpaceEngineException e) {
			LOGGER.catching(Level.WARN, e);
			LOGGER.warn("failed to get tuples for application space {}", id);
			LOGGER.exit();
			return;
		}
		LOGGER.trace("getting lock");
		listenerLock.lock();
		try {
			for (DiscoverListener l : listener) {
				LOGGER.debug("calling {}", l);
				l.applicationModulesDiscovered(id, modules);
			}
		} finally {
			listenerLock.unlock();
			LOGGER.trace("releasing lock");
		}
		LOGGER.exit();
	}

	/**
	 * Enqueues a search for modules in a given application. Listeners will be notified via
	 * {@link DiscoverListener#applicationModulesDiscovered(int, Map)}.
	 * 
	 * @param id
	 *            the id of the application to search in
	 */
	public void startApplicationScan(final int id) {
		LOGGER.entry();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				scanApplication(id);
			}
		};
		addRunnable(runnable);
		LOGGER.exit();
	}

	/**
	 * Adds a listener.
	 * 
	 * @param discoverListener
	 *            the listener to add
	 */
	public void addListener(final DiscoverListener discoverListener) {
		LOGGER.entry(discoverListener);
		listenerLock.lock();
		try {
			LOGGER.trace("adding listener {}", discoverListener);
			listener.add(discoverListener);
		} finally {
			listenerLock.unlock();
		}
		LOGGER.exit();
	}

	/**
	 * Removes a listener.
	 * 
	 * @param discoverListener
	 *            the listener to remove
	 */
	public void removeListener(final DiscoverListener discoverListener) {
		LOGGER.entry(discoverListener);
		listenerLock.lock();
		try {
			LOGGER.trace("removing listener {}", discoverListener);
			listener.remove(discoverListener);
		} finally {
			listenerLock.unlock();
		}
		LOGGER.exit();
	}
}
