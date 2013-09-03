package edu.teco.dnd.eclipse.deployView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.jobs.Job;

import edu.teco.dnd.deploy.Deploy;
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

	private static Collection<DeployJob> jobs = new ArrayList<DeployJob>();

	public static void startDeploying(final String appName, final Deploy deploy,
			final Map<FunctionBlockModel, BlockTarget> mapBlockToTarget) {
		jobs.clear();
		Collection<Module> modules = new ArrayList<Module>();
		for (BlockTarget t : mapBlockToTarget.values()) {
			Module m = t.getModule();
			if (!modules.contains(m)) {
				modules.add(m);
			}
		}

		for (Module m : modules) {
			DeployJob deployJob = new DeployJob(appName, m, deploy);
			deployJob.setUser(true);
			deployJob.schedule();
			jobs.add(deployJob);
		}
		deploy.deploy();
	}

	/**
	 * Cancels all jobs that are invoked in deploying. Shouldn't normally happen, but can be called whenever the plugin
	 * gets shut down while there might still be running jobs. This prevents jobs to be found still running after
	 * platform shutdown and allows the plugin to cancel the jobs itself.
	 */
	public static void cancelDeploying() {
		for (DeployJob j : jobs) {
			if (j != null) {
				j.cancelJob();
			}
		}
	}
}
