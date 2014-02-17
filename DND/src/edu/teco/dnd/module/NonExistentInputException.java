package edu.teco.dnd.module;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;

/**
 * Used to indicate that a requested {@link Input} does not exist on a {@link FunctionBlock}.
 */
public class NonExistentInputException extends Exception {
	private static final long serialVersionUID = -2255346140627213512L;

	public NonExistentInputException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public NonExistentInputException(final String msg) {
		super(msg);
	}

	public NonExistentInputException(final Throwable cause) {
		super(cause);
	}

	public NonExistentInputException() {
		super();
	}
}
