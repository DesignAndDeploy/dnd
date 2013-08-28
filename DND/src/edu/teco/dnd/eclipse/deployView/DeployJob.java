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
import edu.teco.dnd.module.Module;

public class DeployJob extends Job {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DeployJob.class);

	/**
	 * Steps to join a module.
	 */
	public static final int STEPS_JOIN_MODULE = 10;

	/**
	 * Steps to load all used classes to a module.
	 */
	public static final int STEPS_LOAD_CLASSES = 40;

	/**
	 * Steps to load all used blocks to a module.
	 */
	public static final int STEPS_LOAD_BLOCKS = 30;

	/**
	 * Steps to start the module.
	 */
	public static final int STEPS_START_MODULE = 20;

	/**
	 * Total amount of steps for a module, depends on previously defined values.
	 */
	public static final int STEPS_PER_MODULE = STEPS_JOIN_MODULE + STEPS_LOAD_CLASSES + STEPS_LOAD_BLOCKS
			+ STEPS_START_MODULE;
	
	private Module module;
	private Deploy deploy;
	
	public DeployJob(String name, Module m, Deploy d) {
		super(name);
		this.module = m;
		this.deploy = d;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {

		monitor.beginTask("Deploy on: " + module.getName() + " : " + module.getUUID().toString(), STEPS_PER_MODULE);
		
		deploy.addListener(new DeployListener() {

			@Override
			public void moduleJoined(UUID appId, UUID moduleUUID) {
				LOGGER.debug("Module {} joined Application {}", moduleUUID, appId);
				monitor.worked(STEPS_JOIN_MODULE);
			}

			@Override
			public void moduleLoadedClasses(UUID appId, UUID moduleUUID) {
				LOGGER.debug("Module {} loaded all classes for Application {}", moduleUUID, appId);
				monitor.worked(STEPS_LOAD_CLASSES);
			}

			@Override
			public void moduleLoadedBlocks(UUID appId, UUID moduleUUID) {
				LOGGER.debug("Module {} loaded all FunctionBlocks for Application {}", moduleUUID, appId);
				monitor.worked(STEPS_LOAD_BLOCKS);
			}

			@Override
			public void moduleStarted(final UUID appId, final UUID moduleUUID) {
				LOGGER.debug("Module {} started the Application {}", moduleUUID, appId);
				monitor.worked(STEPS_START_MODULE);
			}

			@Override
			public void deployFailed(UUID appId, Throwable cause) {
				LOGGER.debug("deploying Application {} failed: {}", appId, cause);
				monitor.setCanceled(true);
			}
		});
		while (!deploy.getDeployFutureNotifier().isDone()) {
			try {
				deploy.getDeployFutureNotifier().await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return Status.OK_STATUS;
	}

}
