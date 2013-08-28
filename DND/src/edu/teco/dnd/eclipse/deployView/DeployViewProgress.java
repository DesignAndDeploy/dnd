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
import org.eclipse.emf.common.util.Monitor;

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

	public static final int STEPS_JOIN_MODULE = 10;

	public static final int STEPS_LOAD_CLASSES = 40;

	public static final int STEPS_LOAD_BLOCKS = 30;

	public static final int STEPS_START_MODULE = 20;

	public static final int STEPS_PER_MODULE = STEPS_JOIN_MODULE + STEPS_LOAD_CLASSES + STEPS_LOAD_BLOCKS
			+ STEPS_START_MODULE;

	public DeployViewProgress() {
	}

	public void startDeploying(final String appName, final Deploy deploy, final Map<FunctionBlockModel, BlockTarget> mapBlockToTarget) {
		Collection<Module> modules = new ArrayList<Module>();
		for (BlockTarget t: mapBlockToTarget.values()) {
			Module m = t.getModule();
			if (!modules.contains(m)) {
				modules.add(m);
			}
		}
		
		for (Module m : modules){
			Job deployJob = new DeployJob("Deploying " + appName + "...", m, deploy);
			deployJob.setUser(true);
			deployJob.schedule();
		}
		deploy.deploy();
	}
}
