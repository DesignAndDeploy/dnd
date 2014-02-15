package edu.teco.dnd.eclipse.deployEditor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.teco.dnd.deploy.Constraint;
import edu.teco.dnd.deploy.Distribution;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.module.config.BlockTypeHolder;

public class UserConstraints implements Constraint {
	private final Map<FunctionBlockModel, ModuleID> moduleConstraints;
	private final Map<FunctionBlockModel, String> placeConstraints;

	public UserConstraints(Map<FunctionBlockModel, ModuleID> modules, Map<FunctionBlockModel, String> place) {
		this.moduleConstraints = Collections.unmodifiableMap(new HashMap<FunctionBlockModel, ModuleID>(modules));
		this.placeConstraints = Collections.unmodifiableMap(new HashMap<FunctionBlockModel, String>(place));
	}

	@Override
	public boolean isAllowed(Distribution distribution, FunctionBlockModel block, ModuleInfo module, BlockTypeHolder holder) {
		if (moduleConstraints.containsKey(block)) {
			ModuleID moduleID = module.getID();
			if (!moduleConstraints.get(block).equals(moduleID)) {
				return false;
			}
		}

		if (placeConstraints.containsKey(block)) {
			String place = module.getLocation();
			if (!placeConstraints.get(block).equals(place)) {
				return false;
			}
		}

		return true;
	}
}
