package edu.teco.dnd.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.server.ModuleManagerListener;

/**
 * This class manages the registered modules for the command line program.
 * @author jung
 *
 */
public class ModuleRegistrator implements ModuleManagerListener{

	public ModuleRegistrator(){
		System.out.println("Module Registrator created.");
	}
	
	@Override
	public void moduleOnline(UUID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moduleOffline(UUID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moduleResolved(UUID id, Module module) {
		System.out.println("Module registered: " + module.getName() + ":" + id.toString());
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serverOnline(Map<UUID, Module> modules) {
		System.out.println("Server running.");
	}

	@Override
	public void serverOffline() {
		System.out.println("Server offline.");
	}

}
