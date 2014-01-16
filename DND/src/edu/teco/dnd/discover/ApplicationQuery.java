package edu.teco.dnd.discover;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.messages.infoReq.ApplicationListResponse;
import edu.teco.dnd.module.messages.infoReq.BlockID;
import edu.teco.dnd.module.messages.infoReq.RequestApplicationListMessage;
import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

/**
 * class providing methods for querying information about all running applications and also for killing a running
 * application on all Modules.
 * 
 */
public class ApplicationQuery {
	private static final Logger LOGGER = LogManager.getLogger(ApplicationQuery.class);

	/** Maximum amount of time to wait before a kill attempt is assumed to have failed. */
	public static final long MAX_PER_MODULE_KILL_WAIT_TIME = 1000; // (MilliSeconds)
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            The ConnectionManager that will be used to connect to other modules.
	 */
	public ApplicationQuery(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * get a FutureNotifier usable to retrieve a list of running applications (and information about them) from a single
	 * ModuleInfo.
	 * 
	 * @param module
	 *            UUID of the module to query
	 * @return a FutureNotifier usable to retrieve a list of running applications (and information about them)
	 */
	public ModuleApplicationListFutureNotifier getApplicationList(final UUID module) {
		final ModuleApplicationListFutureNotifier notifier = new ModuleApplicationListFutureNotifier(module);
		connectionManager.sendMessage(module, new RequestApplicationListMessage()).addListener(notifier);
		return notifier;
	}

	/**
	 * get a FutureNotifier usable to retrieve a list of running applications (and information about them) from all
	 * connected Modules.
	 * 
	 * @return a FutureNotifier usable to retrieve a list of running applications (and information about them) of all
	 *         modules.
	 */
	public ApplicationListFutureNotifier getApplicationList() {
		final Collection<UUID> modules = connectionManager.getConnectedModules();
		final ApplicationListFutureNotifier notifier = new ApplicationListFutureNotifier(modules.size());
		for (final UUID module : modules) {
			connectionManager.sendMessage(module, new RequestApplicationListMessage()).addListener(notifier);
		}
		return notifier;
	}

	/**
	 * try to kill the application given by appId. Assumes that all modules the app is running on are connected to this
	 * module.
	 * 
	 * @param appId
	 *            the application UUID to be killed.
	 * @return true iff all modules involved replied that the application was successfully killed.
	 */
	public boolean killApplication(UUID appId) {
		ApplicationInformation appInfo;
		ApplicationListFutureNotifier appListNotifier = getApplicationList();

		// get Information about all connected Apps.
		try {
			while (true) {
				try {
					appInfo = appListNotifier.get().get(appId);
				} catch (InterruptedException e) {
					LOGGER.catching(e);
					continue;
				}
				break;
			}
		} catch (ExecutionException e) {
			LOGGER.catching(e);
			LOGGER.warn("Can not kill app {}.");
			return false;
		}

		// for all modules involved in the given appId: send kill(appId) message.
		Map<UUID/* modId */, FutureNotifier<Response>> killFutures = new HashMap<UUID, FutureNotifier<Response>>();
		for (UUID modId : appInfo.getModules()) {
			killFutures.put(modId, connectionManager.sendMessage(modId, new KillAppMessage(appId)));
		}

		// check if all replied success.
		for (Map.Entry<UUID, FutureNotifier<Response>> killNotification : killFutures.entrySet()) {
			while (true) {
				try {
					if (!killNotification.getValue().await(MAX_PER_MODULE_KILL_WAIT_TIME, TimeUnit.MILLISECONDS)
							|| !killNotification.getValue().isSuccess()) {
						LOGGER.warn("killing timed out or not successfull for module {} because of {}.",
								killNotification.getKey(), killNotification.getValue().cause());
						return false;
					}
				} catch (InterruptedException e) {
					continue;
				}
				break;
			}
		}
		return true;
	}

	public static class ModuleApplicationListFutureNotifier extends DefaultFutureNotifier<Map<UUID, String>> implements
			FutureListener<FutureNotifier<Response>> {
		private final UUID moduleUUID;

		public ModuleApplicationListFutureNotifier(final UUID moduleUUID) {
			this.moduleUUID = moduleUUID;
		}

		public UUID getModuleUUID() {
			return this.moduleUUID;
		}

		@Override
		public void operationComplete(FutureNotifier<Response> future) {
			if (future.isSuccess()) {
				final Response response = future.getNow();
				if (response == null || !(response instanceof ApplicationListResponse)) {
					setFailure(new IllegalArgumentException("did not get an ApplicationListResponse"));
				} else {
					setSuccess(((ApplicationListResponse) response).getApplicationNames());
				}
			} else {
				setFailure(future.cause());
			}
		}
	}

	/**
	 * Future notifier used to retireve a list of all running applications.
	 * 
	 * @author Marvin Marx
	 * 
	 */
	public static class ApplicationListFutureNotifier extends
			DefaultFutureNotifier<Map<UUID/* appId */, ApplicationInformation>> implements
			FutureListener<FutureNotifier<Response>> {
		private final AtomicInteger waiting;
		private final ConcurrentMap<UUID/* appId */, ApplicationInformation> appInfos;

		/**
		 * @param waiting
		 *            the amount of returns we do expect. (Usually == the amount of requests we send, doh)
		 */
		public ApplicationListFutureNotifier(final int waiting) {
			if (waiting == 0) {
				this.waiting = null;
				this.appInfos = null;
				setSuccess(new HashMap<UUID, ApplicationInformation>());
			} else {
				this.waiting = new AtomicInteger(waiting);
				this.appInfos = new ConcurrentHashMap<UUID, ApplicationInformation>();
			}
		}

		@Override
		public void operationComplete(final FutureNotifier<Response> future) {
			if (future.isSuccess()) {
				final Response response = future.getNow();
				if (response instanceof ApplicationListResponse) {
					final ApplicationListResponse applicationListResponse = (ApplicationListResponse) response;
					UUID currentModId = applicationListResponse.getModuleUUID();

					Map<UUID, Collection<UUID>> appBlocks = applicationListResponse.getApplicationBlocks();
					Map<UUID, String> appNames = applicationListResponse.getApplicationNames();
					Map<UUID, String> uuidToBlockType = applicationListResponse.getBlockTypes();
					Map<BlockID, String> blockIDToBlockName = applicationListResponse.getBlockNames();
					for (UUID appID : appNames.keySet()) {
						ApplicationInformation currentAppInfo = appInfos.get(appID);
						if (currentAppInfo == null) {
							// application is not yet known
							currentAppInfo = new ApplicationInformation(appID, appNames.get(appID));
							appInfos.put(appID, currentAppInfo);
						}
						for (UUID blockUUID : appBlocks.get(appID)) {
							currentAppInfo.addBlockModulePair(blockUUID, currentModId);
							currentAppInfo.addUUIDBlockTypePair(blockUUID, uuidToBlockType.get(blockUUID));
							currentAppInfo.addBlockIDNamePair(blockUUID, blockIDToBlockName.get(new BlockID(blockUUID, appID)));
						}
					}

				}
			}
			if (waiting.decrementAndGet() <= 0) {
				setSuccess(appInfos);
			}
		}
	}
}
