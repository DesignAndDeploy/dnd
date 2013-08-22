package edu.teco.dnd.blocks;

import java.io.Serializable;

public interface OutputTarget<T extends Serializable> {
	void setValue(T value);
}
