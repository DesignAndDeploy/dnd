package edu.teco.dnd.deploy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.BlockTypeHolder;

public class UserConstraints implements Constraint{

	private Map<FunctionBlock, UUID> moduleConstraints = new HashMap<FunctionBlock, UUID>();
	private Map<FunctionBlock, String> placeConstraints = new HashMap<FunctionBlock, String>();
	
	public UserConstraints(Map<FunctionBlock, UUID> modules, Map<FunctionBlock, String> place){
		this.moduleConstraints = modules;
		this.placeConstraints = place;
	}
	
	
	@Override
	public boolean isAllowed(Distribution distribution, FunctionBlock block,
			Module module, BlockTypeHolder holder) {

		if (moduleConstraints.containsKey(block)){
			UUID id = module.getUUID();
			if (!id.equals(moduleConstraints.get(block))){
				return false;
			}
		}
		
		if (placeConstraints.containsKey(block)){
			String place = module.getLocation();
			if(!place.equals(placeConstraints.get(block))){
				return false;
			}
		}
		
		return true;
	}

}
