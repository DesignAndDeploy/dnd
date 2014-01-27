package edu.teco.dnd.server;

import java.util.ArrayList;
import java.util.Collection;

import edu.teco.dnd.deploy.Constraint;
import edu.teco.dnd.deploy.Distribution;
import edu.teco.dnd.deploy.DistributionGenerator;
import edu.teco.dnd.deploy.MinimalModuleCountEvaluator;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.ModuleInfo;

/**
 * This class creates a distribution, if possible. It might seem a little redundant, since it does not do much more than
 * the normal distribution classes. But it provides re-usable code to all final user programs, e.g. the eclipse
 * application and the command line program.
 * 
 * The exceptions might seem complex, but they are the only way I see to inform clients of this class on possible errors
 * while creating the distribution.
 * 
 * @author jung
 * 
 */
public class DistributionCreator {

	public static Distribution createDistribution(Collection<FunctionBlockModel> blocks,
			Collection<Constraint> constraints) throws NoBlocksException, NoModulesException {
		Collection<ModuleInfo> modules = ServerManager.getDefault().getModuleManager().getModules();
		if (blocks == null || blocks.isEmpty()) {
			throw new NoBlocksException();
		}
		if (modules == null || modules.isEmpty()) {
			throw new NoModulesException();
		}
		if (constraints == null) {
			constraints = new ArrayList<Constraint>();
		}
		DistributionGenerator generator = new DistributionGenerator(new MinimalModuleCountEvaluator(), constraints);
		Distribution dist = generator.getDistribution(blocks, modules);
		return dist;
	}

}
