package edu.teco.dnd.command;

import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.server.ModuleManagerListener;

/**
 * This class displays the registered modules for the command line program.
 * 
 * @author jung
 * 
 */
public class ModuleRegistrator implements ModuleManagerListener {
	@Override
	public void moduleAdded(final ModuleInfo moduleInfo) {
		System.out.println("Module found: " + moduleInfo);
	}

	@Override
	public void moduleRemoved(final ModuleInfo moduleInfo) {
		System.out.println("Module went offline: " + moduleInfo);
	}

	@Override
	public void moduleUpdated(final ModuleInfo moduleInfo) {
		System.out.println("Module was updated: " + moduleInfo);
	}
}
