package edu.teco.dnd.deploy;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.messages.ApplicationBlockMessage;
import edu.teco.dnd.messages.ApplicationBlockStartErrorMessage;
import edu.teco.dnd.messages.ApplicationClassLoadedMessage;
import edu.teco.dnd.messages.ApplicationLoadClassErrorMessage;
import edu.teco.dnd.messages.ApplicationLoadClassMessage;
import edu.teco.dnd.messages.ApplicationModuleMessage;
import edu.teco.dnd.messages.ApplicationStartBlockMessage;
import edu.teco.dnd.messages.GlobalApplicationMessage;
import edu.teco.dnd.messages.GlobalJoinMessage;
import edu.teco.dnd.messages.GlobalModuleMessage;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.util.MuServerProvider;
import edu.teco.dnd.util.RunnerAgent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to distribute an application among the modules in the intelligent environment.
 * 
 */
public class DeploymentAgent extends RunnerAgent {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DeploymentAgent.class);

	/**
	 * The state a block can be in.
	 */
	public static enum Progress {
		/**
		 * GlobalJoinMessage was sent.
		 */
		INVITED,
		/**
		 * ApplicationAgent is started.
		 */
		JOINED,
		/**
		 * All Classes for the block have been loaded.
		 */
		CLASS_LOADED,
		/**
		 * Is Running.
		 */
		RUNNING,
		/**
		 * An Error occured.
		 */
		ERROR
	}

	/**
	 * Reacts to {@link ApplicationModuleMessage} by sending {@link ApplicationLoadClassMessage}s for each
	 * function block the specific module has to start.
	 */
	private class JoinAppListener implements ReactionListener {
		/**
		 * ID used for serialization.
		 */
		private static final long serialVersionUID = -1867034384806812572L;

		/**
		 * Sends {@link ApplicationLoadClassMessage}s when triggered.
		 * 
		 * @param e
		 *            the triggering event tuple
		 */
		@Override
		public void reactsTo(final ReactionEvent e) {
			LOGGER.entry(e);
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					LOGGER.entry();
					sendLoadClassMessages(new ApplicationModuleMessage(e.getEventTuple()));
					LOGGER.exit();
				}
			};
			addRunnable(runnable);
			LOGGER.exit();
		}
	}

	/**
	 * Reacts to {@link ApplicationClassLoadedMessage} by sending {@link ApplicationStartBlockMessage}s for
	 * each function block the specific module has to start.
	 */
	private class LoadClassListener implements ReactionListener {
		/**
		 * ID used for serialization.
		 */
		private static final long serialVersionUID = -1867034384806812572L;

		/**
		 * Sends a {@link ApplicationBlockMessage} when triggered.
		 * 
		 * @param e
		 *            the triggering event tuple
		 */
		@Override
		public void reactsTo(final ReactionEvent e) {
			LOGGER.entry(e);
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					LOGGER.entry();
					sendBlockMessages(e.getEventTuple());
					LOGGER.exit();
				}
			};
			addRunnable(runnable);
			LOGGER.exit();
		}
	}

	/**
	 * Reacts to {@link ApplicationLoadClassErrorMessage} by editing the progress map.
	 */
	private class LoadClassErrorListener implements ReactionListener {

		/**
		 * ID used for serialization.
		 */
		private static final long serialVersionUID = -1867034384806812572L;

		/**
		 * Sets ERROR to the progress map for the function block in the received message.
		 * 
		 * @param e
		 *            the triggering event tuple
		 */
		@Override
		public void reactsTo(final ReactionEvent e) {
			LOGGER.entry(e);
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					LOGGER.entry();
					try {
						lts.in(new ApplicationLoadClassErrorMessage(e.getEventTuple()).getUidMatcherTuple());
					} catch (TupleSpaceEngineException e) {
						LOGGER.catching(Level.INFO, e);
					}
					LOGGER.exit();
				}
			};
			addRunnable(runnable);
			final ApplicationLoadClassErrorMessage msg = new ApplicationLoadClassErrorMessage(
					e.getEventTuple());
			progressLock.writeLock().lock();
			listenerLock.lock();
			for (Map.Entry<FunctionBlock, Module> entry : plan.entrySet()) {
				if (entry.getValue().getID() != msg.getModuleID()) {
					continue;
				}
				FunctionBlock block = entry.getKey();
				if (block.getClass().getName().equals(msg.getClassName())) {
					progress.put(block.getID(), Progress.ERROR);
					informAboutError("could not load " + block.getID() + " because the class "
							+ msg.getClassName() + " failed to load on " + msg.getModuleID());
				}
			}
			listenerLock.unlock();
			progressLock.writeLock().unlock();
			LOGGER.exit();
		}
	}

	/**
	 * Reacts to {@link ApplicationBlockMessage} by editing the progress map.
	 */
	private class StartBlockListener implements ReactionListener {

		/**
		 * ID used for serialization.
		 */
		private static final long serialVersionUID = -1867034384806812572L;

		/**
		 * Sets RUNNING to the progress map for the function block in the received message.
		 * 
		 * @param e
		 *            the triggering event tuple
		 */
		@Override
		public void reactsTo(final ReactionEvent e) {
			LOGGER.entry(e);
			ApplicationBlockMessage blockMsg = new ApplicationBlockMessage(e.getEventTuple());
			progressLock.writeLock().lock();
			progress.put(blockMsg.getBlockID(), Progress.RUNNING);
			progressLock.writeLock().unlock();
			progressChanged();
			LOGGER.exit();
		}
	}

	/**
	 * Reacts to {@link ApplicationBlockStartErrorMessage} by editing the progress map.
	 */
	private class StartBlockErrorListener implements ReactionListener {

		/**
		 * ID used for serialization.
		 */
		private static final long serialVersionUID = -1867034384806812572L;

		/**
		 * Sets ERROR to the progress map for the function block in the received message.
		 * 
		 * @param e
		 *            the triggering event tuple
		 */
		@Override
		public void reactsTo(final ReactionEvent e) {
			LOGGER.entry(e);
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					LOGGER.entry();
					try {
						lts.in(new ApplicationBlockStartErrorMessage(e.getEventTuple()).getUidMatcherTuple());
					} catch (TupleSpaceEngineException e) {
						LOGGER.catching(Level.INFO, e);
					}
					LOGGER.exit();
				}
			};
			addRunnable(runnable);
			final ApplicationBlockStartErrorMessage msg = new ApplicationBlockStartErrorMessage(
					e.getEventTuple());
			progressLock.writeLock().lock();
			progress.put(msg.getBlockID(), Progress.ERROR);
			progressLock.writeLock().unlock();
			informAboutError("failed to start block " + msg.getBlockID() + ". Reason: " + msg.getMessage()
					+ " (caused by " + msg.getCause() + ")");
			progressChanged();
			LOGGER.exit();
		}
	}

	/**
	 * ID used for serialization.
	 */
	private static final long serialVersionUID = -4006892667488608350L;

	/**
	 * The tuple space this class uses.
	 */
	private LimeTupleSpace lts;

	/**
	 * The plan which is used for the correct distribution of the function blocks.
	 */
	private Map<FunctionBlock, Module> plan;

	/**
	 * The name of the application which shall be distributed.
	 */
	private String appName;

	/**
	 * The ID of the application which shall be distributed.
	 */
	private Integer appID;

	/**
	 * Paths to look in for classes.
	 */
	private Set<String> classpath = new HashSet<>();

	/**
	 * This map contains the module IDs as keys and their application agents as values.
	 */
	private Map<Long, AgentID> agents = new HashMap<>();

	/**
	 * This map is used to save the current status of the distribution.
	 */
	private Map<String, Progress> progress = new HashMap<>();

	/**
	 * Used to make access to {@link #progress} and {@link #progressDirty} thread safe.
	 */
	private final ReadWriteLock progressLock = new ReentrantReadWriteLock(true);

	/**
	 * Registered strong reactions.
	 */
	private final Collection<RegisteredReaction> registeredStrongReactions = new ArrayList<>();

	/**
	 * Registered weak reactions.
	 */
	private final Collection<RegisteredReaction> registeredWeakReactions = new ArrayList<>();

	/**
	 * All listeners registered for this agent.
	 */
	private final Set<DeployListener> listener = new HashSet<>();

	/**
	 * Used to make access to {@link #listener} thread safe.
	 */
	private final Lock listenerLock = new ReentrantLock();

	/**
	 * Constructs a new instance of this class.
	 * 
	 * @param plan
	 *            the distribution plan to set
	 * @param appName
	 *            the name of the application to be distributed
	 * @param appID
	 *            the ID of the application to be distributed
	 * @param classpath
	 *            the classpath to use
	 */
	public DeploymentAgent(final HashMap<FunctionBlock, Module> plan, final String appName,
			final Integer appID, final String[] classpath) {
		LOGGER.entry(plan, appName, appID);
		this.plan = plan;
		this.appName = appName;
		this.appID = appID;
		for (String file : classpath) {
			LOGGER.debug("adding class path {}", file);
			this.classpath.add(file);
		}
		LOGGER.exit();
	}

	/**
	 * Informs about an error.
	 * 
	 * @param message
	 *            Message describing the error.
	 */
	private void informAboutError(final String message) {
		listenerLock.lock();
		try {
			for (DeployListener l : listener) {
				l.deployError(message);
			}
		} finally {
			listenerLock.unlock();
		}
	}

	/**
	 * Initializes a new DeploymentAgent.
	 * 
	 * @param plan
	 *            Tells how to map the FunctionBlocks to the modules
	 * @param appName
	 *            Name of the Application
	 * @param appID
	 *            ID of the Application
	 * @param classpath
	 *            Contains the paths of necessary classes.
	 * @return new DeploymentAgent
	 */
	public static DeploymentAgent createAgent(final Map<FunctionBlock, Module> plan, final String appName,
			final Integer appID, final String[] classpath) {
		LOGGER.entry(plan, appName, appID, classpath);
		LimeServer server = LimeServer.getServer();
		DeploymentAgent agent = null;
		String[] cp;
		if (classpath == null) {
			cp = new String[0];
		} else {
			cp = classpath;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Using class path: {}", Arrays.asList(cp));
		}
		try {
			agent = (DeploymentAgent) server.loadAgent(DeploymentAgent.class.getName(), new Serializable[] {
					new HashMap<FunctionBlock, Module>(plan), appName, appID, cp });
		} catch (AgentCreationException e) {
			LOGGER.catching(e);
		}
		LOGGER.exit(agent);
		return agent;
	}

	@Override
	public void doRun() {
		setName("DeploymentAgent" + appID);
		// make sure muserver is running
		MuServerProvider.getMuServer();
		if (!(initTupleSpace("ApplicationSpace" + appID)) || (!initReactions())) {
			LOGGER.error("error starting deployment agent");
			addRunnable(null);
			return;
		}
		informAboutNewApplication();
		informAboutParticipation();
	}

	/**
	 * Sends a LoadClassMessage.
	 * 
	 * @param appMsg
	 *            ApplicationModuleMessage to derive arguments from.
	 */
	private void sendLoadClassMessages(final ApplicationModuleMessage appMsg) {
		LOGGER.entry();
		Set<Class<?>> clss = new HashSet<>();
		agents.put(appMsg.getModuleID(), appMsg.getAgentID());
		LOGGER.debug("setting progress");
		progressLock.writeLock().lock();
		for (Map.Entry<FunctionBlock, Module> entry : plan.entrySet()) {
			if (entry.getValue().getID() == appMsg.getModuleID()) {
				clss.add(entry.getKey().getClass());
				progress.put(entry.getKey().getID(), Progress.JOINED);
			}
		}
		progressLock.writeLock().unlock();
		LOGGER.debug("progress set");
		progressChanged();
		MuServer muserver = MuServerProvider.getMuServer();
		ClassSpace priv = muserver.getPrivateClassSpace();
		ClassSpace shared = muserver.getSharedClassSpace();
		for (Class<?> cls : clss) {
			LOGGER.debug("building message for {}", cls);
			Class<?>[] classes = new Class[] { cls };
			String[] cp = classpath.toArray(new String[0]);
			try {
				classes = ClassInspector.getFullClassClosure(cls.getClassLoader(), cp, cls, muserver);
			} catch (
					ClassNotFoundException | IOException e1) {
				LOGGER.error(e1);
			}
			Set<String> classNames = new HashSet<>();
			for (Class<?> c : classes) {
				classNames.add(c.getName());
				if (shared.containsClass(c.getName())) {
					LOGGER.trace("{} is already in shared space", c);
					continue;
				}
				LOGGER.trace("adding {} to shared space", c);
				try {
					priv.copyClassTo(c.getClassLoader(), cp, c.getName(), shared);
				} catch (DuplicateClassException e) {
					LOGGER.catching(Level.DEBUG, e);
				} catch (ClassNotFoundException e) {
					LOGGER.catching(e);
				}
			}
			ApplicationLoadClassMessage loadMsg = new ApplicationLoadClassMessage(classNames, LimeServer
					.getServer().getLocalAddress().getHostAddress()
					+ ":" + muserver.getPort(), cls.getName());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sending {}", loadMsg.getTuple());
			}
			try {
				lts.out(new AgentLocation(appMsg.getAgentID()), loadMsg.getTuple());
			} catch (TupleSpaceEngineException e) {
				LOGGER.catching(e);
			}
		}
	}

	/**
	 * Sends a BlockMessage.
	 * 
	 * @param tuple
	 *            Tuple to send in the message.
	 */
	private void sendBlockMessages(final ITuple tuple) {
		try {
			lts.in(tuple);
		} catch (TupleSpaceEngineException e) {
			LOGGER.catching(Level.DEBUG, e);
		}
		ApplicationClassLoadedMessage msg = new ApplicationClassLoadedMessage(tuple);
		Collection<FunctionBlock> toSend = new ArrayList<>();
		progressLock.readLock().lock();
		for (Map.Entry<FunctionBlock, Module> entry : plan.entrySet()) {
			FunctionBlock block = entry.getKey();
			if (entry.getValue().getID() == msg.getModuleID()
					&& block.getClass().getName().equals(msg.getClassName())) {
				Progress p = progress.get(block.getID());
				if (p == Progress.INVITED || p == Progress.JOINED) {
					toSend.add(block);
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("{} matches, but status is {}", block, p);
					}
				}
			}
		}
		if (toSend.isEmpty()) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("toSend is empty for class {}, module {}", msg.getClassName(), msg.getModuleID());
			}
			return;
		}
		LOGGER.debug("sending blocks {}", toSend);
		progressLock.readLock().unlock();
		progressLock.writeLock().lock();
		for (FunctionBlock block : toSend) {
			progress.put(block.getID(), Progress.CLASS_LOADED);
		}
		progressLock.writeLock().unlock();
		progressChanged();
		AgentID moduleAgent = agents.get(msg.getModuleID());
		if (moduleAgent == null) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("couldn't find agent for {])", msg.getModuleID());
			}
			return;
		}
		ApplicationStartBlockMessage startMsg = new ApplicationStartBlockMessage();
		Collection<ITuple> tuples = new ArrayList<>();
		for (FunctionBlock block : toSend) {
			startMsg.setFunctionBlock(block);
			ITuple msgtuple = startMsg.getTuple();
			if (msgtuple != null) {
				tuples.add(msgtuple);
			} else {
				LOGGER.warn("IOException with startMsg.getTuple() {}", block.getClass().getName());
				informAboutError("IOException with startMsg.getTuple()");
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("sending {}", tuples);
		}
		try {
			lts.outg(new AgentLocation(moduleAgent), tuples.toArray(new ITuple[0]));
		} catch (TupleSpaceEngineException e) {
			LOGGER.catching(e);
		}
		LOGGER.exit();
	}

	/**
	 * Informs about new Applications.
	 */
	private void informAboutNewApplication() {
		LOGGER.entry();
		try {
			ITuple newApp = new GlobalApplicationMessage(appName, appID).getTuple();
			LimeTupleSpace moduleSpace = new LimeTupleSpace("ModuleSpace");
			moduleSpace.setShared(true);
			for (Module module : new HashSet<>(plan.values())) {
				ITuple idTuple = (new GlobalModuleMessage(module.getID())).getIDTemplate();
				AgentID commID = (new GlobalModuleMessage(moduleSpace.rd(idTuple))).getAgentID();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("sending {} to {}", newApp, moduleSpace);
				}
				moduleSpace.out(new AgentLocation(commID), newApp);
			}
		} catch (TupleSpaceEngineException e) {
			LOGGER.catching(e);
		}
		LOGGER.exit();
	}

	/**
	 * Sends GlobalJoinMessages to all modules that are part of this application.
	 */
	private void informAboutParticipation() {
		LOGGER.entry();
		for (Module module : new HashSet<>(plan.values())) {
			GlobalJoinMessage joinMsg = new GlobalJoinMessage(appID, getMgr().getID());
			ITuple idTuple = (new GlobalModuleMessage(module.getID())).getIDTemplate();
			try {
				LimeTupleSpace moduleSpace = new LimeTupleSpace("ModuleSpace");
				moduleSpace.setShared(true);
				AgentID commID = (new GlobalModuleMessage(moduleSpace.rd(idTuple))).getAgentID();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("sending {} to {} in {}", joinMsg.getTuple(), commID, moduleSpace.getName());
				}
				moduleSpace.out(new AgentLocation(commID), joinMsg.getTuple());
			} catch (TupleSpaceEngineException e) {
				LOGGER.catching(e);
			}
		}
		LOGGER.debug("getting write lock for progress");
		progressLock.writeLock().lock();
		for (FunctionBlock block : plan.keySet()) {
			progress.put(block.getID(), Progress.INVITED);
		}
		progressLock.writeLock().unlock();
		LOGGER.debug("releasing write lock for progress");
		progressChanged();
		LOGGER.exit();
	}

	/**
	 * Initiates the LimeTupleSpace.
	 * 
	 * @param spaceName
	 *            the name of the tuple space
	 * @return false if an error occurred, true otherwise
	 */
	private boolean initTupleSpace(final String spaceName) {
		LOGGER.entry(spaceName);
		try {
			this.lts = new LimeTupleSpace(spaceName);
			lts.setShared(true);
			LimeServer.getServer().engage();
		} catch (
				IllegalTupleSpaceNameException | TupleSpaceEngineException e) {
			LOGGER.exit(false);
			return false;
		}
		LOGGER.exit(true);
		return true;
	}

	/**
	 * Initiates the reactions of this class.
	 * 
	 * @return false if an error occurred, true otherwise
	 */
	private boolean initReactions() {
		Collection<LocalizedReaction> strongReactions = new ArrayList<>();
		Collection<UbiquitousReaction> weakReactions = new ArrayList<>();

		strongReactions.add(new LocalizedReaction(new HostLocation(LimeServer.getServer().getServerID()),
				new AgentLocation(this.getMgr().getID()), new ApplicationClassLoadedMessage().getTemplate(),
				new LoadClassListener(), Reaction.ONCEPERTUPLE));

		weakReactions.add(new UbiquitousReaction(new ApplicationLoadClassErrorMessage().getTemplate(),
				new LoadClassErrorListener(), Reaction.ONCEPERTUPLE));

		weakReactions.add(new UbiquitousReaction(new ApplicationBlockStartErrorMessage().getTemplate(),
				new StartBlockErrorListener(), Reaction.ONCEPERTUPLE));

		weakReactions.add(new UbiquitousReaction(new ApplicationModuleMessage().getTemplate(),
				new JoinAppListener(), Reaction.ONCEPERTUPLE));
		weakReactions.add(new UbiquitousReaction(new ApplicationBlockMessage().getTemplate(),
				new StartBlockListener(), Reaction.ONCEPERTUPLE));

		try {
			for (RegisteredReaction r : lts.addWeakReaction(weakReactions.toArray(new Reaction[0]))) {
				registeredWeakReactions.add(r);
			}
			for (RegisteredReaction r : lts.addStrongReaction(strongReactions
					.toArray(new LocalizedReaction[0]))) {
				registeredStrongReactions.add(r);
			}
		} catch (TupleSpaceEngineException e) {
			return false;
		}
		return true;
	}

	/**
	 * Removes the reactions that were installed into the tuple space.
	 */
	private void shutdown() {
		try {
			lts.removeStrongReaction(registeredStrongReactions.toArray(new RegisteredReaction[0]));
		} catch (
				TupleSpaceEngineException | NoSuchReactionException e) {
			LOGGER.catching(e);
		}
		try {
			lts.removeWeakReaction(registeredWeakReactions.toArray(new RegisteredReaction[0]));
		} catch (
				TupleSpaceEngineException | NoSuchReactionException e) {
			LOGGER.catching(e);
		}
	}

	/**
	 * Called if the progress has been updated. Informs listeners and shuts down the agent if all blocks'
	 * state is either RUNNING or ERROR.
	 */
	private void progressChanged() {
		LOGGER.entry();
		int classesLoaded = 0;
		int blocksStarted = 0;
		boolean finished = true;
		LOGGER.debug("acquiring read lock");
		progressLock.readLock().lock();
		for (Progress p : progress.values()) {
			switch (p) {
			case RUNNING:
				blocksStarted++;
				classesLoaded++;
				break;

			case CLASS_LOADED:
				classesLoaded++;
				// no break
			default:
				finished = false;
			}
		}
		LOGGER.debug("releasing read lock");
		progressLock.readLock().unlock();
		LOGGER.debug("acquiring listener lock");
		listenerLock.lock();
		try {
			for (DeployListener l : listener) {
				LOGGER.debug("updating {}", l);
				l.updateDeployStatus(classesLoaded, blocksStarted);
			}
		} finally {
			listenerLock.unlock();
			LOGGER.debug("releasing listener lock");
		}
		if (finished) {
			LOGGER.debug("shutting down");
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					shutdown();
				}
			};
			addAndEnd(runnable);
		}
	}

	/**
	 * Adds a listener.
	 * 
	 * @param l
	 *            DeployListener to add.
	 */
	public void addListener(final DeployListener l) {
		LOGGER.entry(l);
		listenerLock.lock();
		try {
			listener.add(l);
		} finally {
			listenerLock.unlock();
		}
		LOGGER.exit();
	}

	/**
	 * Removes a listener.
	 * 
	 * @param l
	 *            DeployListener to add.
	 */
	public void removeListener(final DeployListener l) {
		LOGGER.entry(l);
		listenerLock.lock();
		try {
			listener.remove(l);
		} finally {
			listenerLock.unlock();
		}
		LOGGER.exit();
	}
}
