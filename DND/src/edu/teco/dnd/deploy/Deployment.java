package edu.teco.dnd.deploy;

import java.util.Collection;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;

/**
 * This interface defines the access to an optimal deployment. The classes
 * implementing this interface can define their own algorithm to find an optimal
 * deployment and decide which criteria make their deployment optimal.
 * 
 * @author jung
 * 
 */
public interface Deployment {

	/**
	 * Computes a Deployment that maps each FunctionBlock on a module to run on
	 * 
	 * @param functionBlocks
	 *            Collection of function blocks to be deployed
	 * @param modules
	 *            available modules
	 * @return Map that assigns one module to each function block
	 */
	public Map<FunctionBlock, Module> getDeployment(
			Collection<FunctionBlock> functionBlocks, Collection<Module> modules);

	/**
	 * Computes a deployment that maps each FunctionBlock on a module to run on,
	 * in case a Collection of FunctionBlocks and Modules has been passed to
	 * this class before. Can be used to get a new and different deployment for
	 * the same function blocks and modules, optimized for the same criteria.
	 * 
	 * @return Map that assigns one module to each function block, in case they already were passed to the class.
	 */
	public Map<FunctionBlock, Module> getDeployment();
}
