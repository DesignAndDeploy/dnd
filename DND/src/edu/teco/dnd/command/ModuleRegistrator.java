package edu.teco.dnd.command;

import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.server.ModuleManagerListener;

/**
 * This class displays the registered modules for the command line program.
 * @author jung
 *
 */
public class ModuleRegistrator implements ModuleManagerListener{

	@Override
	public void moduleOnline(UUID id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moduleOffline(UUID id, ModuleInfo module) {
		System.out.print("ModuleInfo went offline: ");
		if (module != null){
			System.out.print(module.getName() + " ");
		}
		System.out.println(": " + id.toString());
	}

	@Override
	public void moduleResolved(UUID id, ModuleInfo module) {
		System.out.println("ModuleInfo registered: " + module.getName() + ":" + id.toString());
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serverOnline(Map<UUID, ModuleInfo> modules) {
		System.out.println("Server running.");
	}

	@Override
	public void serverOffline() {
		System.out.println("Server offline.");
	}

}
