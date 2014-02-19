package edu.teco.dnd.module;

import edu.teco.dnd.blocks.FunctionBlock;

/**
 * Used to indicate that a requested {@link FunctionBlock} does not exist.
 */
public class NonExistentFunctionblockException extends Exception {
	private static final long serialVersionUID = -2255346140627213512L;

	public NonExistentFunctionblockException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public NonExistentFunctionblockException(final String msg) {
		super(msg);
	}

	public NonExistentFunctionblockException(final Throwable cause) {
		super(cause);
	}

	public NonExistentFunctionblockException() {
		super();
	}
}
