package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.UUID;

import lights.interfaces.ITuple;
import lights.interfaces.TupleSpaceException;
import lime.AgentCreationException;
import lime.AgentLocation;
import lime.HostLocation;
import lime.IllegalTupleSpaceNameException;
import lime.LimeServer;
import lime.LimeTupleSpace;
import lime.LocalizedReaction;
import lime.Reaction;
import lime.ReactionEvent;
import lime.ReactionListener;
import lime.TupleSpaceEngineException;

import edu.teco.dnd.messages.GlobalApplicationMessage;
import edu.teco.dnd.messages.GlobalJoinMessage;
import edu.teco.dnd.messages.GlobalModuleMessage;
import edu.teco.dnd.util.RunnerAgent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles communication that is not application specific.
 */
public class CommunicationAgent extends RunnerAgent {

	/**
	 * Reacts to JoinMessages by calling {@link CommunicationAgent#joinApplication(GlobalJoinMessage)}.
	 */
	private class JoinListener implements ReactionListener {

		/**
		 * Used for serialization.
		 */
		private static final long serialVersionUID = -5151256134756174041L;

		/**
		 * Calls {@link CommunicationAgent#joinApplication(GlobalJoinMessage)}.
		 * 
		 * @param e
		 *            ReactionEvent
		 */
		@Override
		public void reactsTo(final ReactionEvent e) {
			Module.getLocalModule().getCommunicationAgent()
					.joinApplication(new GlobalJoinMessage(e.getEventTuple()));
		}
	}

	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 5183789824404053872L;

	/**
	 * Used for logging.
	 */
	private static final Logger LOGGER = LogManager.getLogger(CommunicationAgent.class);

	/**
	 * The module space this agent operates on.
	 */
	private LimeTupleSpace moduleSpace;

	/**
	 * ModuleMessage which we currently have in the ModuleSpace.
	 */
	private GlobalModuleMessage currentModuleMsg = null;

	/**
	 * constructor (should only be used once per module).
	 */
	public CommunicationAgent() {
		super();
		if (Module.getLocalModule() != null) {
			assert Module.getLocalModule().getCommunicationAgent() == null;
			// should only be called once by Module.
		}
	}

	/**
	 * Connects to the ModuleSpace and sends a {@link GlobalModuleMessage}. It also generates an ID for the
	 * local module.
	 */
	@Override
	public void doRun() {
		LOGGER.debug("started new AppAgent");
		try {
			moduleSpace = new LimeTupleSpace("ModuleSpace");

		} catch (TupleSpaceEngineException e) {
			throw new IllegalStateException("Lime not working properly.", e);
		} catch (IllegalTupleSpaceNameException e) {
			assert false;
		}
		moduleSpace.setShared(true);
		LimeServer.getServer().engage();

		LOGGER.info("ModuleSpace engaged.");

		Module module = Module.getLocalModule();
		module.setID(UUID.randomUUID().getLeastSignificantBits());

		ITuple joinTemplate = (new GlobalJoinMessage()).getTemplate();
		LocalizedReaction[] react = { new LocalizedReaction(new HostLocation(LimeServer.getServer()
				.getServerID()), new AgentLocation(this.getMgr().getID()), joinTemplate, new JoinListener(),
				Reaction.ONCEPERTUPLE) };

		currentModuleMsg = new GlobalModuleMessage(module, this.getMgr().getID());
		try {
			moduleSpace.addStrongReaction(react);
			moduleSpace.out(currentModuleMsg.getTuple());
		} catch (TupleSpaceEngineException e) {
			throw new IllegalStateException("Cannot put announcement tuple into space.", e);
		}
		LOGGER.info("Module tuple send and listening for App requests.");
	}

	/**
	 * Starts an {@link ApplicationAgent} for a new application.
	 * 
	 * @param message
	 *            the message that triggered the event
	 */
	private void joinApplication(final GlobalJoinMessage message) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Serializable[] param = { message.getApplicationID(), message.getDeploymentAgentID() };
				try {
					moduleSpace.in(message.getUidMatcherTuple());
					LimeServer.getServer().loadAgent("edu.teco.dnd.module.ApplicationAgent", param);
					LOGGER.info("started AppAgent for ID {}", param[0]);
				} catch (
						AgentCreationException | TupleSpaceEngineException e) {
					LOGGER.error("Can not start appagent/remove tuple.");
					LOGGER.catching(e);
				}
			}
		};
		addRunnable(runnable);
	}

	/**
	 * called by module, whenever an application is stopped.
	 * 
	 * @param appSpaceId
	 *            id of the application stopped.
	 */
	void stopApplication(final Integer appSpaceId) { // packageAccess
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					ITuple[] tuples = moduleSpace.rdg(new AgentLocation(getMgr().getID()),
							AgentLocation.UNSPECIFIED, (new GlobalApplicationMessage()).getTemplate());
					if (tuples == null) {
						throw new TupleSpaceException();
					}
					for (ITuple t : tuples) {
						GlobalApplicationMessage msg = new GlobalApplicationMessage(t);
						if (msg.getApplicationID().equals(appSpaceId)) {
							moduleSpace.inp(new AgentLocation(getMgr().getID()), AgentLocation.UNSPECIFIED,
									msg.getTuple());
						}
					}

				} catch (
						TupleSpaceEngineException | TupleSpaceException e) {
					LOGGER.catching(e);
					LOGGER.warn("Can not remove GlobalApplicationMessage from ModuleSpace.");
				}
			}
		};
		addRunnable(runnable);
	}

	/**
	 * used before shutdown of the module. Does necessary communication cleanup.
	 */
	void shutdown() { // package access
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					moduleSpace.in(currentModuleMsg.getTemplate());
				} catch (TupleSpaceEngineException e) {
					LOGGER.catching(e);
					LOGGER.error("Cannot remove my module Message while shutting down.");
				}
			}
		};
		addAndEnd(runnable);
	}

	/**
	 * triggered by Module whenever it has changed its configuration.
	 */
	void moduleChanged() { // package access
		LOGGER.entry();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				LOGGER.entry();
				try {
					ITuple tuple = moduleSpace.inp(new AgentLocation(getMgr().getID()),
							AgentLocation.UNSPECIFIED, currentModuleMsg.getTemplate());
					if (tuple == null) {
						LOGGER.warn("could not remove old GlobalModuleMessage");
					}
					currentModuleMsg = new GlobalModuleMessage(Module.getLocalModule(), Module
							.getLocalModule().getCommunicationAgent().getMgr().getID());
					moduleSpace.out(currentModuleMsg.getTuple());
					LOGGER.info("Module tuple renewed");
				} catch (TupleSpaceEngineException e) {
					LOGGER.fatal("Cannot put reannouncement tuple into space. Exiting!");
					addRunnable(null);
				}
				LOGGER.exit();
			}
		};
		addRunnable(runnable);
		LOGGER.exit();
	}
}
