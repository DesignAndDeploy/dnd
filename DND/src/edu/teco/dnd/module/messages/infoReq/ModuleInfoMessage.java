package edu.teco.dnd.module.messages.infoReq;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.network.messages.Message;

public class ModuleInfoMessage implements Message {
	public static final String MESSAGE_TYPE = "ModuleInformation";
	public final Module mod;
	
	public ModuleInfoMessage(ConfigReader conf, ModuleApplicationManager appManager){
		mod = new Module(conf.getUuid(), conf.getName(), conf.getBlockRoot());
	}

}
