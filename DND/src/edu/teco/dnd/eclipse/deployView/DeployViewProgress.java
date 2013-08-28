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

	public static void startDeploying(final String appName, final Deploy deploy, final Map<FunctionBlockModel, BlockTarget> mapBlockToTarget) {
		Collection<Module> modules = new ArrayList<Module>();
		for (BlockTarget t: mapBlockToTarget.values()) {
			Module m = t.getModule();
			if (!modules.contains(m)) {
				modules.add(m);
			}
		}
		
		for (Module m : modules){
			Job deployJob = new DeployJob(appName, m, deploy);
			deployJob.setUser(true);
			deployJob.schedule();
		}
		deploy.deploy();
	}
}
