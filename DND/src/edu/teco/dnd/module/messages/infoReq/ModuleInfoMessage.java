package edu.teco.dnd.module.messages.infoReq;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.network.messages.Response;

public class ModuleInfoMessage extends Response {
	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "module info";
	public final Module module;

	public ModuleInfoMessage(final ConfigReader conf) {
		module = new Module(conf.getUuid(), conf.getName(), conf.getBlockRoot());
	}

	public Module getModule() {
		return this.module;
	}
}
