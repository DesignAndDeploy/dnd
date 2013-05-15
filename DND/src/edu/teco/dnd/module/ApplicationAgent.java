package edu.teco.dnd.module;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lights.adapters.Tuple;
import lights.interfaces.ITuple;
import lime.AgentID;
import lime.AgentLocation;
import lime.HostLocation;
import lime.IllegalTupleSpaceNameException;
import lime.LimeServer;
import lime.LimeServerID;
import lime.LimeSystemTupleSpace;
import lime.LimeTupleSpace;
import lime.LocalizedReaction;
import lime.NoSuchReactionException;
import lime.Reaction;
import lime.ReactionEvent;
import lime.ReactionListener;
import lime.RegisteredReaction;
import lime.TupleSpaceEngineException;
import mucode.ClassSpace;
import mucode.abstractions.Relocator;

import edu.teco.dnd.blocks.ConnectionTarget;
import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.InvalidFunctionBlockException;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.blocks.SystemTimer;
import edu.teco.dnd.blocks.Timer;
import edu.teco.dnd.messages.ApplicationBlockMessage;
import edu.teco.dnd.messages.ApplicationClassLoadedMessage;
import edu.teco.dnd.messages.ApplicationKillMessage;
import edu.teco.dnd.messages.ApplicationLoadClassErrorMessage;
import edu.teco.dnd.messages.ApplicationLoadClassMessage;
import edu.teco.dnd.messages.ApplicationModuleMessage;
import edu.teco.dnd.messages.ApplicationStartBlockMessage;
import edu.teco.dnd.messages.ApplicationValueMessage;
import edu.teco.dnd.messages.Message;
import edu.teco.dnd.util.MuServerProvider;
import edu.teco.dnd.util.RunnerAgent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles application specific communication.
 */
public class ApplicationAgent extends RunnerAgent {

	/**
	 * {@link ApplicationAgent#loadClass(ApplicationLoadClassMessage)}.
	 */
	private class LoadClassListener implements ReactionListener {
		/**
		 * Used to check the version of this class when deserialising.
		 */
		private static final long serialVersionUID = -4988562957215665178L;

		/**
		 * Calls {@link ApplicationAgent#loadClass(ApplicationLoadClassMessage)} .
		 * 
		 * @param e
		 *            the triggering event tuple.
		 */
		@Override
		public void reactsTo(final ReactionEvent e) {
			loadClass(new ApplicationLoadClassMessage(e.getEventTuple()));
		}
	}

	/**
	 * Reacts to {@link ApplicationStartBlockMessage}s by calling
	 * {@link ApplicationAgent#startBlock(ApplicationStartBlockMessage)}.
	 */
	private class StartBlockListener implements ReactionListener {
		/**
		 * Used to check the version of this class when deserialising.
		 */
		private static final long serialVersionUID = -6987542973980075588L;

		/**
		 * Calls {@link ApplicationAgent#startBlock(ITuple)}.
		 * 
		 * @param e
		 *            the triggering event tuple.
		 */
		@Override
		public void reactsTo(final ReactionEvent e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(e.getEventTuple());
			}
			startBlock(e.getEventTuple());
		}
	}

	/**
	 * Reacts to {@link ApplicationValueMessage}s by calling
	 * {@link ApplicationAgent#setLocalValue(ApplicationValueMessage)}.
	 */
	private class ValueListener implements ReactionListener {
		/**
		 * Used to check the version of this class when deserialising.
		 */
		private static final long serialVersionUID = -2467937571734500260L;

		/**
		 * Calls {@link ApplicationAgent#setLocalValue(ApplicationValueMessage)}.
		 * 
		 * @param e
		 *            the triggering event tuple.
		 */
		@Override
		public void reactsTo(final ReactionEvent e) {
			setLocalValue(new ApplicationValueMessage(e.getEventTuple()));
		}
	}

	/**
	 * Reacts to {@link ApplicationKillMessage}s by calling
	 * {@link ApplicationAgent#kill(ApplicationKillMessage)}.
	 * 
	 */
	private class KillListener implements ReactionListener {
		/**
		 * Used to check the version of this class when deserialising.
		 */
		private static final long serialVersionUID = 5055899308721539523L;

		/**
		 * Calls {@link ApplicationAgent#kill(ApplicationKillMessage)}. Does not run in same thread as the one
		 * calling reactsTo.
		 * 
		 * @param e
		 *            the triggering event tuple.
		 */
		@Override
		public void reactsTo(final ReactionEvent e) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					kill(new ApplicationKillMessage(e.getEventTuple()));
				}
			};
			addAndEnd(runnable);
		}
	}

	/**
	 * Maximum time appAgent waits for a class to be loaded into its space in milliseconds. Gives up after
	 * that time.
	 */
	public static final long MAX_SLEEP = 30L * 1000L;

	/**
	 * in how many subphases the sleep to wait for a class to be loaded by muCode is divided. (recheck done
	 * between each subphase).
	 */
	public static final long SLEEP_PHASES = 30L;

	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = -851710023013823561L;

	/**
	 * Used for logging.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ApplicationAgent.class);

	/**
	 * the scheduler this appAgent uses.
	 */
	private final Scheduler scheduler = new Scheduler();

	/**
	 * the id of the space this AppAgent is in.
	 */
	private final Integer appSpaceId;

	/**
	 * The tuple space used for this application.
	 */
	private LimeTupleSpace applicationSpace;
	/**
	 * convenience variable pointing to the local Module.
	 */
	private final Module localModule;

	/**
	 * The AgentID of the DeploymentAgent.
	 */
	private final AgentID deploymentAgentID;

	/**
	 * mapping of active blocks to their ID, used e.g. to pass values to inputs.
	 */
	private Map<String, FunctionBlock> funcBlockById = new HashMap<String, FunctionBlock>();

	/**
	 * mapping of function block IDs to the matching application agent.
	 */
	private final Map<String, AgentID> agentsByBlock = new HashMap<>();

	/**
	 * the reactions we registered with lime (so we can delete them during shutdown).
	 */
	private RegisteredReaction[] activeLimeReactions;

	/**
	 * read locks are held by all functions working with the local data. The writelock is aquired by kill()
	 * when it shuts down the appAgent.
	 */
	private ReentrantReadWriteLock shutdownlock = new ReentrantReadWriteLock();

	/**
	 * Timeout for class loading.
	 */
	private final Timer timeoutTimer = new SystemTimer(MAX_SLEEP);

	/**
	 * @param appSpaceId
	 *            int identifying the applicationSpace to join
	 * @param deploymentAgentID
	 *            the ID of the DeploymentAgent
	 */
	public ApplicationAgent(final Integer appSpaceId, final AgentID deploymentAgentID) {
		if (appSpaceId == null || deploymentAgentID == null) {
			throw new IllegalArgumentException("Application agent initialised with null pointers.");
		}
		this.appSpaceId = appSpaceId;
		this.deploymentAgentID = deploymentAgentID;
		this.localModule = Module.getLocalModule();

		MuServerProvider.getMuServer();
	}

	/**
	 * Connects to the application tuple space and sends a ApplicationModuleMessage.
	 */
	@Override
	public void doRun() {
		ITuple msgTemplate;
		Collection<LocalizedReaction> limeReactions = new ArrayList<>();

		try {
			applicationSpace = new LimeTupleSpace("ApplicationSpace" + appSpaceId);
		} catch (TupleSpaceEngineException e) {
			throw new IllegalStateException("Lime not working properly.", e);
		} catch (IllegalTupleSpaceNameException e) {
			assert false;
		}
		applicationSpace.setShared(true);
		LimeServer.getServer().engage();
		LOGGER.info("ApplicationSpace" + appSpaceId + " engaged.");

		// preparing the reactions:

		msgTemplate = (new ApplicationKillMessage()).getTemplate();
		limeReactions.add(new LocalizedReaction(new HostLocation(LimeServer.getServer().getServerID()),
				new AgentLocation(this.getMgr().getID()), msgTemplate, new KillListener(),
				Reaction.ONCEPERTUPLE));
		msgTemplate = (new ApplicationLoadClassMessage()).getTemplate();
		limeReactions.add(new LocalizedReaction(new HostLocation(LimeServer.getServer().getServerID()),
				new AgentLocation(this.getMgr().getID()), msgTemplate, new LoadClassListener(),
				Reaction.ONCEPERTUPLE));
		msgTemplate = (new ApplicationStartBlockMessage()).getTemplate();
		limeReactions.add(new LocalizedReaction(new HostLocation(LimeServer.getServer().getServerID()),
				new AgentLocation(this.getMgr().getID()), msgTemplate, new StartBlockListener(),
				Reaction.ONCEPERTUPLE));
		msgTemplate = (new ApplicationValueMessage()).getTemplate();
		limeReactions.add(new LocalizedReaction(new HostLocation(LimeServer.getServer().getServerID()),
				new AgentLocation(this.getMgr().getID()), msgTemplate, new ValueListener(),
				Reaction.ONCEPERTUPLE));

		ApplicationModuleMessage msg = new ApplicationModuleMessage(localModule.getID(), this.getMgr()
				.getID(), localModule.getModuleConfig());
		try {
			activeLimeReactions = applicationSpace.addStrongReaction(limeReactions
					.toArray(new LocalizedReaction[0]));
			LOGGER.info("AppAgent reactions set up in AppSpace {}", appSpaceId);
			ITuple tup = msg.getTuple();
			applicationSpace.out(tup);
		} catch (TupleSpaceEngineException e) {
			LOGGER.catching(Level.ERROR, e);
			LOGGER.error("Can not insert ApplicationModuleMessage/reactions in tuplespace.");
		}
		LOGGER.info("ApplicationModule tuple send to (appSpaceid {})", appSpaceId);

	}

	/**
	 * Loads the class specified in the message.
	 * 
	 * @param message
	 *            the message containing the class to load
	 */
	private void loadClass(final ApplicationLoadClassMessage message) {

		try {
			if (!shutdownlock.readLock().tryLock(0, TimeUnit.SECONDS)) { // can not risc blocking lime.
				return; // we are shutting down, so its useless anyway.
			}
		} catch (InterruptedException e1) {
			return; // we would not ever wait, unless writelocked, meaning shutting down
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("LoadClass tuple received ({})", message.getClassNames());
		}

		// dispatching reply message
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				LOGGER.entry();
				String muServer = message.getMuServer();
				Set<String> classNames = message.getClassNames();
				Message replyMsg;

				ClassSpace shared = MuServerProvider.getMuServer().getSharedClassSpace();

				Exception thrownException = null;
				try {
					for (String className : classNames) {
						LOGGER.info("Loading {}.class . Loading from {}", className, muServer);
						Class<?> cls = null;
						try {
							cls = Class.forName(className);
						} catch (ClassNotFoundException e) {
							// proceed below.
						}
						if (cls == null) {
							new Relocator(MuServerProvider.getMuServer()).fetchClasses(muServer,
									new String[] { className }, Relocator.ROOT, false);

							timeoutTimer.reset();
							while (!shared.containsClass(className)) {
								long toSleep = timeoutTimer.getTimeToNextTick() / SLEEP_PHASES;
								if (toSleep <= 0) {
									toSleep = 1;
								}
								try {
									Thread.sleep(toSleep);
								} catch (InterruptedException e) {
								}
								if (timeoutTimer.check()) {
									LOGGER.warn("Waiting for class {} to be loaded into space timed out.",
											className);
									throw new TimeoutException("Waiting for class " + className
											+ " to be loaded into space timed out.");
								}
							}
							cls = shared.getClass(className);
						}
						if (cls == null) {
							throw new ClassNotFoundException();
						}
						// everything went fine:
						LOGGER.info("Class " + className + " succesfully loaded.");
					}
				} catch (
						ClassNotFoundException | IOException | TimeoutException e) {

					LOGGER.error("Can not load {}.class", message.getMainClass());
					LOGGER.catching(e);
					thrownException = e;
				}

				if (thrownException == null) {
					replyMsg = new ApplicationClassLoadedMessage(localModule.getID(), message.getMainClass());
				} else {
					replyMsg = new ApplicationLoadClassErrorMessage(message.getMainClass(),
							"Cannot pull class into sharedSpace (app: " + appSpaceId + ")", thrownException,
							localModule.getID());
				}
				try {
					LOGGER.debug("removing load class message");
					ITuple tup = applicationSpace.inp(new AgentLocation(getMgr().getID()),
							AgentLocation.UNSPECIFIED, message.getUidMatcherTuple());
					if (tup == null) {
						LOGGER.info("Can not remove Load class Msg.");
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("sending reply {} to {}", replyMsg.getTuple(), deploymentAgentID);
					}
					applicationSpace.out(new AgentLocation(deploymentAgentID), replyMsg.getTuple());
				} catch (TupleSpaceEngineException e) {
					LOGGER.error("Can not answer to classLoadMessage or remove the message.");
					LOGGER.catching(e);
				}
				LOGGER.exit();
			}
		};
		addRunnable(runnable);

		shutdownlock.readLock().unlock();
	}

	/**
	 * Starts the {@link FunctionBlock}.
	 * 
	 * @param tuple
	 *            the tuple received containing the FunctionBlock to start
	 */
	private void startBlock(final ITuple tuple) {
		LOGGER.entry();
		try {
			if (!shutdownlock.readLock().tryLock(0, TimeUnit.SECONDS)) { // can not risc blocking lime.
				return; // we are shutting down, so its useless anyway.
			}
		} catch (InterruptedException e1) {
			return; // we would not ever wait, unless writelocked, meaning shutting down
		}
		final ApplicationStartBlockMessage message = new ApplicationStartBlockMessage(tuple);
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("startBlock {} tuple received", message.getFunctionBlock().getType());
		}
		final FunctionBlock block = message.getFunctionBlock();
		if (!Module.getLocalModule().startBlock(block)) {
			LOGGER.error("Can not start block ({}) on this module.(appspace{})", message.getFunctionBlock()
					.getType(), appSpaceId);
			return;
		}
		try {
			for (Output<?> output : block.getOutputs().values()) {
				for (ConnectionTarget ct : output.getConnectedTargets()) {
					if (ct instanceof RemoteConnectionTarget) {
						RemoteConnectionTarget rct = (RemoteConnectionTarget) ct;
						rct.setApplicationAgent(this);
					}
				}
			}
		} catch (InvalidFunctionBlockException e1) {
			LOGGER.catching(e1);
		}
		scheduler.addFunctionBlock(block);
		funcBlockById.put(block.getID(), block);

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				LOGGER.entry();
				try {
					LOGGER.trace("removing message tuple");
					ITuple t = applicationSpace.inp(new AgentLocation(getMgr().getID()),
							AgentLocation.UNSPECIFIED, message.getUidMatcherTuple());
					if (t == null) {
						LOGGER.warn("failed to remove tuple");
					}
					LOGGER.trace("creating reply message");
					ApplicationBlockMessage msg = new ApplicationBlockMessage(getMgr().getID(), block.getID());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("sending {}", msg.getTuple());
					}
					applicationSpace.out(msg.getTuple());
					LOGGER.info("removed StartBlockMsg from space ({} from appSpace{})", message
							.getFunctionBlock().getType(), appSpaceId);
				} catch (TupleSpaceEngineException e) {
					LOGGER.error("Can not removed ValueMsg tuple.");
					LOGGER.catching(e);
				}
				LOGGER.exit();
			}
		};
		addRunnable(runnable);
		shutdownlock.readLock().unlock();
		LOGGER.exit();
	}

	/**
	 * Sets the input of a local connection target.
	 * 
	 * @param message
	 *            the message containing the target and the new value
	 */
	private void setLocalValue(final ApplicationValueMessage message) {

		try {
			if (!shutdownlock.readLock().tryLock(0, TimeUnit.SECONDS)) { // can not risc blocking lime.
				return; // we are shutting down, so its useless anyway.
			}
		} catch (InterruptedException e1) {
			return; // we would not ever wait, unless writelocked, meaning shutting down
		}

		LOGGER.debug("appValue tuple received for {}", message.getFunctionBlockID());
		if (funcBlockById.get(message.getFunctionBlockID()) == null) {
			LOGGER.warn("FunctionBlockID not existent. ({})", message.getFunctionBlockID());
			return;
		}
		ConnectionTarget ct = funcBlockById.get(message.getFunctionBlockID()).getConnectionTargets()
				.get(message.getInput());
		if (ct == null) {
			LOGGER.warn("specified input does not exist: {} on {}", message.getInput(),
					message.getFunctionBlockID());

		} else {
			ct.setValue(message.getValue());
		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					ITuple tuple = applicationSpace.inp(new AgentLocation(getMgr().getID()),
							AgentLocation.UNSPECIFIED, message.getUidMatcherTuple());
					if (tuple == null) {
						LOGGER.warn("failed to remove value tuple");
					} else {
						LOGGER.debug("removed ValueMsg from space ({} from appSpace {})",
								message.getFunctionBlockID(), appSpaceId);
					}
				} catch (TupleSpaceEngineException e) {
					LOGGER.error("Can not removed ValueMsg tuple.");
					LOGGER.catching(e);
				}
			}
		};
		addRunnable(runnable);
		shutdownlock.readLock().unlock();
	}

	/**
	 * Kills the application. This will stop all FunctionBlocks, then leave the tuple space.
	 * 
	 * @param message
	 *            the message that triggered the shutdown
	 */
	private void kill(final ApplicationKillMessage message) {

		// this is/must be(!) executed inside a runnnable, not by lime.

		LOGGER.info("kill tuple received");
		try {
			applicationSpace.removeStrongReaction(activeLimeReactions);
		} catch (
				TupleSpaceEngineException | NoSuchReactionException e) {
			LOGGER.warn("Can not remove registered reaction from space (appSpace{})", appSpaceId);
			LOGGER.catching(e);
		}

		// we will not release this. This is intentional.
		shutdownlock.writeLock().lock();

		for (FunctionBlock block : funcBlockById.values()) {
			localModule.stopBlock(block);
		}
		funcBlockById.clear();
		scheduler.stopRunning();

		try {
			applicationSpace.in(message.getTuple());
			LOGGER.info("removed KillMsg from space (appSpace {})", appSpaceId);

			applicationSpace.in(new AgentLocation(this.getMgr().getID()), AgentLocation.UNSPECIFIED,
					(new ApplicationModuleMessage()).getTemplate());
		} catch (TupleSpaceEngineException e) {
			LOGGER.error("Can not removed ValueMsg tuple.");
			LOGGER.catching(e);
		}

		localModule.stopApplication(appSpaceId);

	}

	/**
	 * Sends a new value to a remote ConnectionTarget.
	 * 
	 * @param targetFunctionBlock
	 *            the ID of the FunctionBlock the ConnectionTarget belongs to
	 * @param targetInput
	 *            the name of the Input the ConnectionTarget belongs to
	 * @param value
	 *            the new value
	 */
	public void setRemoteValue(final String targetFunctionBlock, final String targetInput,
			final Serializable value) {
		try {
			if (!shutdownlock.readLock().tryLock(0, TimeUnit.SECONDS)) { // can not risc blocking lime.
				return; // we are shutting down, so its useless anyway.
			}
		} catch (InterruptedException e1) {
			return; // we would not ever wait, unless writelocked, meaning shutting down
		}

		final ApplicationValueMessage msg = new ApplicationValueMessage(targetFunctionBlock, targetInput,
				value);

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				AgentID target = agentsByBlock.get(targetFunctionBlock);
				if (target == null) {
					ITuple tuple = null;
					try {
						tuple = tryGet(applicationSpace,
								new ApplicationBlockMessage(targetFunctionBlock).getBlockIDTemplate());
					} catch (TupleSpaceEngineException e) {
						LOGGER.catching(e);
						return;
					}
					if (tuple == null) {
						LOGGER.error("target module not found for block {}", targetFunctionBlock);
						return;
					}
					ApplicationBlockMessage abm = new ApplicationBlockMessage(tuple);
					target = abm.getAgentID();
					agentsByBlock.put(targetFunctionBlock, target);
				}
				try {
					LOGGER.debug("sending value for {}:{} to {} (value: {})", targetFunctionBlock,
							targetInput, target, value);
					applicationSpace.out(new AgentLocation(target), msg.getTuple());
				} catch (TupleSpaceEngineException e) {
					LOGGER.error("Can not put ValueMsg in appSpace{}.", appSpaceId);
					LOGGER.catching(e);
				}
			}
		};
		addRunnable(runnable);

		shutdownlock.readLock().unlock();

	}

	/**
	 * Tries to find a tuple matching the specified template on all servers that have a (shared) TupleSpace
	 * with the given name. The tuple is copied from the TupleSpace.
	 * 
	 * @param space
	 *            the space to look in
	 * @param template
	 *            the template to look for
	 * @return a tuple matching the template if one exists, null otherwise
	 * @throws TupleSpaceEngineException
	 *             if the underlying TupleSpaceEngine encounters an error
	 */
	private static ITuple tryGet(final LimeTupleSpace space, final ITuple template)
			throws TupleSpaceEngineException {
		LOGGER.entry(space, template);
		LimeSystemTupleSpace lsts = new LimeSystemTupleSpace();
		ITuple[] spaces = lsts.rdg(new Tuple().addFormal(Object.class).addActual(space.getName())
				.addFormal(AgentID.class));
		if (spaces == null) {
			LOGGER.exit(null);
			return null;
		}
		Set<LimeServerID> servers = new HashSet<>();
		for (ITuple tuple : spaces) {
			servers.add(((AgentID) tuple.get(2).getValue()).getLimeServerID());
		}
		for (LimeServerID id : servers) {
			LOGGER.trace("Trying {}", id);
			ITuple tuple = space.rdp(new HostLocation(id), AgentLocation.UNSPECIFIED, template);
			if (tuple != null) {
				LOGGER.exit(tuple);
				return tuple;
			}
		}
		LOGGER.exit(null);
		return null;
	}
}
