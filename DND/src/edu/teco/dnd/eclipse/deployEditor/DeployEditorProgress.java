package edu.teco.dnd.eclipse.deployEditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import edu.teco.dnd.deploy.Deploy;
import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.util.FutureNotifier;

/**
 * This class displays the progress of deploying (later: also creating?) a distribution.
 * 
 * @author jung
 * 
 */
public class DeployEditorProgress {

	private static FutureNotifier<Void> n;

	public static void startDeploying(final String appName, final Deploy deploy,
			final Map<FunctionBlockModel, BlockTarget> mapBlockToTarget) {
		Collection<ModuleInfo> modules = new ArrayList<ModuleInfo>();
		for (BlockTarget t : mapBlockToTarget.values()) {
			ModuleInfo m = t.getModule();
			if (!modules.contains(m)) {
				modules.add(m);
			}
		}

		for (ModuleInfo m : modules) {
			DeployJob deployJob = new DeployJob(appName, m, deploy);
			deployJob.setUser(true);
			deployJob.schedule();
		}
		deploy.deploy();
		n = deploy.getDeployFutureNotifier();
	}

	/**
	 * Cancels all jobs that are invoked in deploying. Shouldn't normally happen, but can be called whenever the plugin
	 * gets shut down while there might still be running jobs. This prevents jobs to be found still running after
	 * platform shutdown and allows the plugin to cancel the jobs itself.
	 */
	public static void cancelDeploying() {
		if (n != null && !n.isDone()) {
			n.cancel(true);
		}
	}
}
