package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.ConnectionTarget;
import edu.teco.dnd.blocks.FunctionBlock;

public class Application {

	private static final Logger LOGGER = LogManager.getLogger(Application.class);

	private UUID ownAppId;
	public final String name;
	/**
	 * the scheduler this appAgent uses.
	 */
	private final Scheduler scheduler = new Scheduler();

	/**
	 * mapping of active blocks to their ID, used e.g. to pass values to inputs.
	 */
	private Map<String, FunctionBlock> funcBlockById = new HashMap<String, FunctionBlock>();

	/**
	 * @return all blocks, this app is currently executing.
	 */
	public Collection<FunctionBlock> getAllBlocks() {
		return funcBlockById.values();
	}

	public Application(UUID appId, UUID deployingAgentId, String name) {
		ownAppId = appId;
		this.name = name;
	}

	/**
	 * loads a class into this app
	 * 
	 * @param classnames
	 *            name of the class to load
	 * @param mainclassname
	 *            mainclass (the reason we are loading this
	 * @return true on success.s
	 */
	public boolean loadClass(Set<String> classnames, String mainclassname) {
		// TODO request class from networkpart of app.
		// TODO is this needed?
		return false;
	}

	/**
	 * starts the given function block on the Module. Also triggers removing it
	 * from runnable blocks
	 * 
	 * @param block
	 *            the block to be started.
	 * @return true iff block was successfully started.
	 */
	public boolean startBlock(FunctionBlock block) {
		scheduler.addFunctionBlock(block);
		funcBlockById.put(block.getID(), block);
		return true;
	}

	/**
	 * passes a received value the given input of a local block.
	 * 
	 * @param funcBlockId
	 *            Id of the block to pass the message to.
	 * @param input
	 *            input on the block receiving the message.
	 * @param value
	 *            the value to give to the input.
	 * @return true iff value was successfully passed on.
	 */
	public boolean receiveValue(String funcBlockId, String input, Serializable value) {
		if (funcBlockById.get(funcBlockId) == null) {
			LOGGER.info("FunctionBlockID not existent. ({})", funcBlockId);
			return false;
		}
		ConnectionTarget ct = funcBlockById.get(funcBlockId).getConnectionTargets().get(input);
		if (ct == null) {
			LOGGER.warn("specified input does not exist: {} on {}", input, funcBlockId);
			return false;
		}
		ct.setValue(value);
		return true;
	}

	/**
	 * called to indicate, that the application is being shut down. Quits the
	 * scheduling of it.
	 * 
	 * @return
	 */
	public boolean shutdown() {
		scheduler.stopRunning();
		return true;
	}
}
