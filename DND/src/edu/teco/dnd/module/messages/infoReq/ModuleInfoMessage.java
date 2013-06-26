package edu.teco.dnd.module.messages.infoReq;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.network.messages.Response;

public class ModuleInfoMessage extends Response {
	public static final String MESSAGE_TYPE = "module info";
	public final Module module;

	public ModuleInfoMessage(ConfigReader conf, ModuleApplicationManager appManager) {
		module = new Module(conf.getUuid(), conf.getName(), conf.getBlockRoot());
	}

	public Module getModule() {
		return this.module;
	}
}