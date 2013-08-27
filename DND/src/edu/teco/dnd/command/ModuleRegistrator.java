package edu.teco.dnd.command;

import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.server.ModuleManagerListener;
import edu.teco.dnd.server.ServerManager;

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
	public void moduleOffline(UUID id) {
		Map<UUID, Module> map = ServerManager.getDefault().getModuleManager().getMap();
		System.out.print("Module went offline: ");
		if (map.get(id) != null){
			System.out.print(map.get(id).getName() + " ");
		}
		System.out.println(": " + id.toString());
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
