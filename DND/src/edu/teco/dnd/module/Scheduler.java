package edu.teco.dnd.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.teco.dnd.blocks.FunctionBlock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Takes care of updating a number of FunctionBlocks.
 */
public class Scheduler {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);

	/**
	 * Maps from FunctionBlock ID to Updater.
	 */
	private final Map<String, Updater> updaters = new HashMap<String, Updater>();

	public final int maxAllowedThreads;
	private final ExecutorService updaterPool;
	/**
	 * <app uuid, functionBlocks of the app>
	 */
	private Map<UUID /* appid */, Set<FunctionBlock>> runnableBlocks;
	private Map<UUID/* appid */, Set<FunctionBlock>> blocksNeedingInit = new HashMap<UUID, Set<FunctionBlock>>();
	private Set<UUID/*appid*/> applist = new HashSet<UUID>();

	public Scheduler(int maxAllowedThreads) {
		this.maxAllowedThreads = maxAllowedThreads;
		updaterPool = Executors.newFixedThreadPool(maxAllowedThreads);
		for (int i = 0; i < maxAllowedThreads; i++) {
			updaterPool.execute(new Updater(applist, runnableBlocks, blocksNeedingInit));
		}
	}

	/**
	 * Adds a FunctionBlock to update. An app exists in the system exactly when
	 * at least one function block of it exists.
	 * 
	 * @param functionBlock
	 *            the FunctionBlock to update
	 */
	public final void addFunctionBlock(final FunctionBlock functionBlock, UUID appId) {
		if (functionBlock == null || appId == null) {
			throw new IllegalArgumentException("functionBlock/appID must not be null");
		}
		
		if(!applist.contains(appId)){
			applist.add(appId);
			blocksNeedingInit.put(appId, new HashSet<FunctionBlock>());
			runnableBlocks.put(appId, new HashSet<FunctionBlock>());
		}
		
		blocksNeedingInit.get(appId).add(functionBlock); //TODO upon init put into runnableBlocks list

		LOGGER.info("Started updater for {}", functionBlock.getID());
	}

	/**
	 * Notifies the Scheduler that a ConnectionTarget of a FunctionBlock has
	 * changed.
	 * 
	 * @param id
	 *            the ID of the block
	 */
	public final void notifyChanged(final String id) {
		Updater updater = updaters.get(id);
		if (updater != null) {
			//TODO fix this. updater.notifyChanged();
		}
	}

	/**
	 * Stops updating FunctionBlocks.
	 */
	public final void stopRunning() {
		LOGGER.info("stopping all updaters");
		for (Updater updater : updaters.values()) {
			updater.stopRunning();
		}
	}



}
