package edu.teco.dnd.blocks;

import java.util.UUID;

public class ValueDestination {
	private final UUID block;
	private final String input;
	
	public ValueDestination(final UUID block, final String input) {
		this.block = block;
		this.input = input;
	}
	
	public UUID getBlock() {
		return this.block;
	}
	
	public String getInput() {
		return this.input;
	}
}