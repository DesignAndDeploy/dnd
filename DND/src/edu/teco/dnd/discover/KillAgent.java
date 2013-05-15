package edu.teco.dnd.discover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import edu.teco.dnd.messages.ApplicationKillMessage;
import edu.teco.dnd.messages.ApplicationModuleMessage;
import edu.teco.dnd.module.ApplicationAgent;
import edu.teco.dnd.util.RunnerAgent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An agent that sends {@link ApplicationKillMessage} to all agents specified in a given job.
 * 
 * @author peter
 */
public class KillAgent extends RunnerAgent {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 8074319147987179064L;

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(KillAgent.class);

	/**
	 * The single instance of this class that should be used.
	 */
	private static KillAgent singleton = null;

	/**
	 * The system tuple space that stores information about connected servers and agents.
	 */
	private LimeSystemTupleSpace systemTupleSpace = null;

	/**
	 * Returns the instance of KillAgent that should be used. If no instance exists yet one is loaded with
	 * Lime. If this fails, null is returned.
	 * 
	 * @return the instance of KillAgent that should be used or null if loading a KillAgent failed
	 */
	public static synchronized KillAgent getSingleton() {
		if (singleton == null) {
			try {
				singleton = (KillAgent) LimeServer.getServer().loadAgent(KillAgent.class.getName(), null);
			} catch (AgentCreationException e) {
				LOGGER.catching(Level.WARN, e);
			}
		}
		return singleton;
	}

	/**
	 * Public constructor so Lime can load this class. Use {@link #getSingleton()} instead.
	 */
	public KillAgent() {
	}

	@Override
	public void doRun() {
		systemTupleSpace = new LimeSystemTupleSpace();
	}

	/**
	 * Kills the application with the given id.
	 * 
	 * @param id
	 *            the application id of the application to kill
	 */
	private void kill(final int id) {
		LimeTupleSpace applicationSpace = null;
		try {
			applicationSpace = new LimeTupleSpace("ApplicationSpace" + id);
		} catch (
				IllegalTupleSpaceNameException | TupleSpaceEngineException e) {
			LOGGER.catching(e);
			LOGGER.error("failed to joing application space {}", id);
			return;
		}
		applicationSpace.setShared(true);
		LimeServer.getServer().engage();
		ITuple killMessage = new ApplicationKillMessage().getTemplate();
		try {
			for (AgentID agentID : getAllAgentIDs(applicationSpace)) {
				applicationSpace.out(new AgentLocation(agentID), killMessage);
			}
		} catch (TupleSpaceEngineException e) {
			LOGGER.catching(e);
			LOGGER.error("failed to kill application {}", id);
		}
	}

	/**
	 * Returns the AgentIDs of all {@link ApplicationAgent}s that are part of the given ApplicationSpace.
	 * 
	 * @param applicationSpace
	 *            the application space to search in
	 * @return the AgentIDs of all ApplicationAgents found
	 * @throws TupleSpaceEngineException
	 *             if the underlying TupleSpaceEngine encounters a problem
	 */
	private Set<AgentID> getAllAgentIDs(final LimeTupleSpace applicationSpace)
			throws TupleSpaceEngineException {
		ApplicationModuleMessage amm = new ApplicationModuleMessage();
		Set<AgentID> agentIDs = new HashSet<>();
		for (ITuple tuple : getAllTuples(applicationSpace, amm.getTemplate())) {
			amm.setTuple(tuple);
			agentIDs.add(amm.getAgentID());
		}
		return agentIDs;
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
		ITuple[] spaces = systemTupleSpace.rdg(new Tuple().addFormal(Object.class).addActual(space.getName())
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
	 * Enqueues a job to kill the application with the given id.
	 * 
	 * @param id
	 *            the id of the application to kill
	 */
	public void killApplication(final int id) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				kill(id);
			}
		};
		addRunnable(runnable);
	}
}
