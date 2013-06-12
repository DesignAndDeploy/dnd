package edu.teco.dnd.module;

import java.util.HashMap;
import java.util.UUID;

import edu.teco.dnd.module.config.BlockTypeHolder;

/**
 * Represents a Module
 * 
 * @author jung
 * 
 */
public class Module {
	private UUID id;
	private String name;
	private BlockTypeHolder holder;

	/**
	 * Creates a new representation of a module.
	 * 
	 * @param id
	 *            UUID of the module.
	 * @param name
	 *            Name of the module.
	 * @param holder
	 *            BlockTypeHolder of the module.
	 */
	public Module(UUID id, String name, BlockTypeHolder holder) {
		this.id = id;
		this.name = name;
		this.holder = holder;
	}

	/**
	 * Returns the BlockTypeHolder of this module.
	 * 
	 * @return BlockTypeHolder of this module.
	 */
	public BlockTypeHolder getHolder() {
		return this.holder;
	}
	
	/**
	 * Returns Map from available Types to amount of available slots.
	 * @return Map from Types to amount of free slots for this type
	 */
	public HashMap<String, Integer> getTypes(){
		return holder.getTypes();
	}
	
	public boolean tryAddFunctionBlock(String functType){
		if(holder.getTypes().containsKey(functType)){
			
		}
		return false;
	}
}
