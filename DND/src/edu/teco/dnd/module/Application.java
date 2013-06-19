package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.ConnectionTarget;
import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.InvalidFunctionBlockException;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.module.messages.values.AppValueMessage;
import edu.teco.dnd.network.ConnectionManager;

public class Application {

	private static final Logger LOGGER = LogManager.getLogger(Application.class);

	public final UUID ownAppId;
	public final String name;
	private final ScheduledThreadPoolExecutor scheduledThreadPool;
	private final ConnectionManager connMan;

	/**
	 * mapping of active blocks to their ID, used e.g. to pass values to inputs.
	 */
	private final Map<String, FunctionBlock> funcBlockById = new HashMap<String, FunctionBlock>();

	/**
	 * @return all blocks, this app is currently executing.
	 */
	public Collection<FunctionBlock> getAllBlocks() {
		return funcBlockById.values();
	}

	public Application(UUID appId, UUID deployingAgentId, String name, ScheduledThreadPoolExecutor scheduledThreadPool,
			ConnectionManager connMan) {
		this.ownAppId = appId;
		this.name = name;
		this.scheduledThreadPool = scheduledThreadPool;
		this.connMan = connMan;
	}

	/**
	 * called from this app, when a value is supposed to be send to another block (potentially on another Module).
	 * 
	 * @param funcBlock
	 *            the receiving functionBlock.
	 * @param input
	 *            the input on the given block to receive the message.
	 * @param val
	 *            the value to be send.
	 * @return true iff setting was successful.
	 */
	public void sendValue(String funcBlock, String input, Serializable value) {
		UUID modUid = null; // FIXME the uuid of the mod this funBlock is on. HOW to find out? (see X1)
		assert false; //Not implemented
		
		AppValueMessage message = new AppValueMessage(ownAppId, funcBlock, input, value);
		connMan.sendMessage(modUid, message);
		return;
	}

	/**
	 * loads a class into this app
	 * 
	 * @param classnames
	 *            name of the class to load
	 * @param classData
	 *            bytecode of the class to be loaded
	 */
	public void loadClass(String classnames, byte[] classData) {

		// TODO implement class loading
		throw new NotImplementedException();
	}

	/**
	 * starts the given function block on the Module. Also triggers removing it from runnable blocks
	 * 
	 * @param block
	 *            the block to be started.
	 * @return true iff block was successfully started.
	 */
	public boolean startBlock(final FunctionBlock block) {
		funcBlockById.put(block.getID(), block);
		scheduledThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					block.init();

					for (Output<?> output : block.getOutputs().values()) {
						for (ConnectionTarget ct : output.getConnectedTargets()) {
							if (ct instanceof RemoteConnectionTarget) {
								RemoteConnectionTarget rct = (RemoteConnectionTarget) ct;
								rct.setApplication(Application.this);
							}
						}
					}
				} catch (InvalidFunctionBlockException e) {
					LOGGER.warn("User supplied block {} initialization failed.", block.getID());
					LOGGER.catching(e);

				}
			}
		});
		Runnable updater = new Runnable() {

			@Override
			public void run() {
				try {
					block.doUpdate();
				} catch (AssignmentException e) {
					LOGGER.catching(e);
				}

			}
		};
		long period = block.getTimebetweenSchedules();
		try {
			if (period < 0) {
				scheduledThreadPool.schedule(updater, 0, TimeUnit.SECONDS);
			} else {
				scheduledThreadPool.scheduleAtFixedRate(updater, period, period, TimeUnit.SECONDS);
			}
		} catch (RejectedExecutionException e) {
			LOGGER.info("Received start block after initiating shutdown. Not scheduling block {}.", block);
		}

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
	 * @throws IllegalAccessException
	 * @throws NonExistentFunctionblockException
	 * @throws NonExistentInputException
	 */
	public void receiveValue(final String funcBlockId, String input, Serializable value)
			throws NonExistentFunctionblockException, NonExistentInputException {

	 	if (funcBlockById.get(funcBlockId) == null) {
			LOGGER.info("FunctionBlockID not existent. ({})", funcBlockId);
			throw LOGGER.throwing(new NonExistentFunctionblockException());
		}
		ConnectionTarget ct = funcBlockById.get(funcBlockId).getConnectionTargets().get(input);
		if (ct == null) {
			LOGGER.warn("specified input does not exist: {} on {}", input, funcBlockId);
			throw LOGGER.throwing(new NonExistentInputException());
		}
		ct.setValue(value);
		Runnable updater = new Runnable() {

			@Override
			public void run() {
				try {
					funcBlockById.get(funcBlockId).doUpdate();
				} catch (AssignmentException e) {
					LOGGER.catching(e);
				}

			}
		};
		try {
			scheduledThreadPool.schedule(updater, 0, TimeUnit.SECONDS);
		} catch (RejectedExecutionException e) {
			LOGGER.info("Received message after initiating shutdown. Not rescheduling {}.", funcBlockId);
		}
	}

	/**
	 * called to indicate, that the application is being shut down. Quits the scheduling of it.
	 * 
	 * @return
	 */
	public void shutdown() {
		scheduledThreadPool.shutdown();
	}

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return scheduledThreadPool.awaitTermination(timeout, unit);
	}

	public UUID getOwnAppId() {
		return ownAppId;
	}
}
