package edu.teco.dnd.blocks;

import java.io.Serializable;

public class Input<T extends Serializable> {
	public T popValue() {
		return null;
	}
	
	public boolean hasMoreValues() {
		return false;
	}
}
