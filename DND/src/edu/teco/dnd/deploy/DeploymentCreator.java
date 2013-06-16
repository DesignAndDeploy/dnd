package edu.teco.dnd.deploy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.BlockTypeHolder;

/**
 * First Prototype of a class that can create deployments
 * 
 * @author jung
 * 
 */
public class DeploymentCreator implements Deployment {
	private Collection<FunctionBlock> blocks;
	private Collection<Module> modules;
	private Map<FunctionBlock, Module> deployment;

	public DeploymentCreator(Collection<FunctionBlock> functionBlocks,
			Collection<Module> modules) {
		this.blocks = functionBlocks;
		this.modules = modules;
		deployment = new HashMap<FunctionBlock, Module>();
	}

	@Override
	public Map<FunctionBlock, Module> getDeployment(
			Collection<FunctionBlock> functionBlocks, Collection<Module> modules) {
		deployment = new HashMap<FunctionBlock, Module>();
		if (deploymentExists(functionBlocks, modules)) {
			return this.deployment;
		}
		return null;
	}

	@Override
	public Map<FunctionBlock, Module> getDeployment() {
		deployment = new HashMap<FunctionBlock, Module>();
		if (deploymentExists(this.blocks, this.modules)) {
			return this.deployment;
		}
		return null;
	}

	/**
	 * Checks if a collection of functionBlocks can be deployed on a collection
	 * of modules (recursively) If possible, the deployment is created and
	 * stored in the attribute deployment.
	 * 
	 * Attention: this algorithm requires that a type of a functionBlock only
	 * appears in one leaf of the BlockHolderType-Tree. If a type of a
	 * functionBlock appears in more than one leaf it might happen that not all
	 * of these leaves are checked.
	 * 
	 * @param functionBlocks
	 *            Blocks to deploy
	 * @param modules
	 *            Modules to deploy on
	 * @return true if deployment exists.
	 */
	private boolean deploymentExists(Collection<FunctionBlock> functionBlocks,
			Collection<Module> modules) {
		if (functionBlocks == null || functionBlocks.isEmpty()) {
			return true;
		}
		Iterator<FunctionBlock> i = functionBlocks.iterator();
		FunctionBlock block = i.next();
		String blockType = block.getType();						    // Hole Block aus Liste
		for (Module m : modules) { 									// Für alle Module:
			BlockTypeHolder holder = m.getHolder();				    // Hole BlockTypeHolder des Moduls
			if (putBlockOnModule(blockType, holder)) { 				// Falls ein Modul bzw. Platz auf den Holder für den Block existiert
				functionBlocks.remove(block); 						// Nimm den Block aus der Liste
																	// für zu verteilende Blöcke
				if (deploymentExists(functionBlocks, modules)) { 	// Falls eine Verteilung für die restliche Liste existiert
					deployment.put(block, m);					    // erstelle Verteilung
					return true; 									// return true
				}
				increaseHolder(holder, blockType); 					// BlockTypeHolder für den Typ wieder erhöhen!
				functionBlocks.add(block); 							// Sonst: Block wieder in die Liste rein
			}
		}
		return false;
	}

	/**
	 * - alle Module durchgehen und Slot für FBlock suchen - Sobald ein Slot
	 * gefunden: Decrease, fb aus Collection rausnehmen, rekursiv auf rest
	 * aufrufen - Falls das true zurückgibt: true zurückgeben - falls false:
	 * rückgängig machen, weiter nach Slots in Modulen suchen - Falls keine
	 * Slots mehr zur verfügung: return false
	 */

	private boolean putBlockOnModule(String blockType, BlockTypeHolder holder) {
		String moduleType = holder.getType();
		if (moduleType != null && moduleType.equals(blockType)) {
			return holder.tryDecrease();
		} else if (holder.getChildren() != null) {
			for (BlockTypeHolder h : holder.getChildren()) {
				if (putBlockOnModule(blockType, h)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Used to increase counter for Type in a BlocktypeHolder, if a deployment
	 * attempt didn't work out.
	 * 
	 * @param holder
	 *            holder to increase
	 * @param type
	 *            type to increase counter for
	 * @return true if succeeded, false if something went wrong /
	 *         BlockTypeHolder didn't contain a leaf with the given type / ...
	 */
	private boolean increaseHolder(BlockTypeHolder holder, String type) {
		String moduleType = holder.getType();
		if (moduleType != null && moduleType.equals(type)) {
			holder.increase();
			return true;
		} else {
			for (BlockTypeHolder h : holder.getChildren()) {
				if (increaseHolder(h, type)) {
					return true;
				}
			}
		}
		return false;
	}

}
