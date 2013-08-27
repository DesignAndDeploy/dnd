package edu.teco.dnd.eclipse.deployView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import edu.teco.dnd.deploy.Deploy;
import edu.teco.dnd.deploy.DeployListener;
import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.Module;

/**
 * This class displays the progress of deploying (later: also creating?) a distribution.
 * 
 * @author jung
 * 
 */
public class DeployViewProgress {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DeployViewProgress.class);

	public static final int STEPS_JOIN_MODULE = 15;

	public static final int STEPS_LOAD_CLASSES = 35;

	public static final int STEPS_LOAD_BLOCKS = 35;

	public static final int STEPS_START_MODULE = 20;

	public static final int STEPS_PER_MODULE = STEPS_JOIN_MODULE + STEPS_LOAD_CLASSES + STEPS_LOAD_BLOCKS
			+ STEPS_START_MODULE;

	public DeployViewProgress() {
	}

	public void startDeploying(final String appName, final Deploy deploy, final Map<FunctionBlockModel, BlockTarget> mapBlockToTarget) {
		Job job = new Job(appName) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				Collection<Module> modules = new ArrayList<Module>();
				for (BlockTarget t: mapBlockToTarget.values()) {
					Module m = t.getModule();
					if (!modules.contains(m)) {
						modules.add(m);
					}
				}

				monitor.beginTask("Deploy all", modules.size() * STEPS_PER_MODULE);

				final Map<UUID, SubProgressMonitor> idToMonitor = new HashMap<UUID, SubProgressMonitor>();

				for (Module m : modules) {
					SubProgressMonitor spm = new SubProgressMonitor(monitor, STEPS_PER_MODULE);
					spm.setTaskName("Deploy on: " + m.getName() + " : " + m.getUUID().toString());
					idToMonitor.put(m.getUUID(), spm);
				}

				deploy.addListener(new DeployListener() {

					@Override
					public void moduleJoined(UUID appId, UUID moduleUUID) {
						LOGGER.debug("Module {} joined Application {}", moduleUUID, appId);
						SubProgressMonitor m = idToMonitor.get(moduleUUID);
						m.worked(STEPS_JOIN_MODULE);
					}

					@Override
					public void moduleLoadedClasses(UUID appId, UUID moduleUUID) {
						LOGGER.debug("Module {} loaded all classes for Application {}", moduleUUID, appId);
						SubProgressMonitor m = idToMonitor.get(moduleUUID);
						m.worked(STEPS_LOAD_CLASSES);
					}

					@Override
					public void moduleLoadedBlocks(UUID appId, UUID moduleUUID) {
						LOGGER.debug("Module {} loaded all FunctionBlocks for Application {}", moduleUUID, appId);
						SubProgressMonitor m = idToMonitor.get(moduleUUID);
						m.worked(STEPS_LOAD_BLOCKS);
					}

					@Override
					public void moduleStarted(final UUID appId, final UUID moduleUUID) {
						LOGGER.debug("Module {} started the Application {}", moduleUUID, appId);
						SubProgressMonitor m = idToMonitor.get(moduleUUID);
						m.worked(STEPS_START_MODULE);
					}

					@Override
					public void deployFailed(UUID appId, Throwable cause) {
						LOGGER.debug("deploying Application {} failed: {}", appId, cause);
					}
				});
				deploy.deploy();
				while (!deploy.getDeployFutureNotifier().isDone()) {
					try {
						deploy.getDeployFutureNotifier().await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return Status.OK_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();
	}
}
