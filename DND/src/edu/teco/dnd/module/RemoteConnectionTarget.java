package edu.teco.dnd.module;

import java.io.Serializable;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.ConnectionTarget;

// TODO: implement class
public class RemoteConnectionTarget extends ConnectionTarget {
	private static final long serialVersionUID = -4588323454242345616L;

	public RemoteConnectionTarget(String name, String id, String name2,
			Class<? extends Serializable> loadClass) {
		super(name);
	}

	@Override
	public Class<? extends Serializable> getType() {
		return null;
	}

	@Override
	public void setValue(Serializable value) {
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void update() throws AssignmentException {
	}
}
